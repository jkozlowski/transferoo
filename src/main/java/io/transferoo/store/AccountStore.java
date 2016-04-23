/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakub Dominik Kozlowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.transferoo.store;

import com.google.common.base.Preconditions;
import io.transferoo.api.Account;
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.ErrorCode;
import io.transferoo.api.Transaction;
import io.transferoo.api.TransactionAccountType;
import io.transferoo.api.TransactionMetadata;
import io.transferoo.api.UniqueId;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.concurrent.ThreadSafe;

/**
 * All methods are globally synchronized: not ideal, but this is the simplest implementation.
 */
@ThreadSafe
public class AccountStore {

    private final Map<UniqueId<Account>, Account> accounts = new HashMap<>();
    private final Map<UniqueId<Transaction>, Transaction> transactions = new HashMap<>();

    public Account createAccount(AccountMetadata metadata) {
        UniqueId<Account> accountId = UniqueId.of(UUID.randomUUID());
        Preconditions.checkState(!accounts.containsKey(accountId),
                                 "Oh noes, UUIDs just clashed! Lucky you! Try again later!");
        Account account = Account.builder()
                                 .id(accountId)
                                 .metadata(metadata)
                                 .build();
        accounts.put(accountId, account);
        return account;
    }

    public synchronized Optional<Account> getAccountById(UniqueId<Account> accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public synchronized Transaction createTransaction(TransactionMetadata metadata) {

        if (sourceAccountIsSameAsDestination(metadata)) {
            throw ErrorCode.sourceSameAsDestinationException(metadata);
        }

        if (transactionAmountNotPositive(metadata)) {
            throw ErrorCode.transactionAmountNonPositiveException(metadata);
        }

        Account source = getAccountByIdStrict(metadata.source(), TransactionAccountType.SOURCE);
        Account destination = getAccountByIdStrict(metadata.destination(), TransactionAccountType.DESTINATION);

        if (!hasEnoughBalance(metadata, source)) {
            throw ErrorCode.insufficientBalanceException(metadata, source);
        }

        Transaction transaction = putTransaction(metadata);

        BigDecimal amount = metadata.amount();
        modifyBalance(source, (balance) -> balance.subtract(amount, MathContext.UNLIMITED));
        modifyBalance(destination, (balance) -> balance.add(amount, MathContext.UNLIMITED));

        return transaction;
    }

    public synchronized Optional<Transaction> getTransactionById(UniqueId<Transaction> transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    private Account getAccountByIdStrict(UniqueId<Account> accountId, TransactionAccountType accountType) {
        return getAccountById(accountId).orElseThrow(ErrorCode.unknownAccountId(accountId, accountType));
    }

    private boolean sourceAccountIsSameAsDestination(TransactionMetadata metadata) {
        return metadata.source().equals(metadata.destination());
    }

    private boolean transactionAmountNotPositive(TransactionMetadata metadata) {
        return metadata.amount().compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean hasEnoughBalance(TransactionMetadata metadata, Account source) {
        return metadata.amount().compareTo(source.metadata().balance()) <= 0;
    }

    private Transaction putTransaction(TransactionMetadata metadata) {
        UniqueId<Transaction> transactionId = UniqueId.of(UUID.randomUUID());
        Preconditions.checkState(!transactions.containsKey(transactionId),
                                 "Oh noes, UUIDs just clashed! Lucky you! Try again later!");
        Transaction transaction = Transaction.builder()
                                             .id(transactionId)
                                             .metadata(metadata)
                                             .build();
        transactions.put(transactionId, transaction);

        return transaction;
    }

    private void modifyBalance(Account account, Function<BigDecimal, BigDecimal> modifier) {
        Preconditions.checkState(accounts.containsKey(account.id()),
                                 "Should've checked that %s exists, you naughty developer!",
                                 account.id());

        accounts.put(account.id(), account.newBalance(modifier.apply(account.metadata().balance())));
    }
}
