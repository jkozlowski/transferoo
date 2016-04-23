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

package io.transferoo;

import static org.assertj.core.api.Assertions.assertThat;

import io.transferoo.api.Account;
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.ErrorCode;
import io.transferoo.api.Transaction;
import io.transferoo.api.TransactionMetadata;
import io.transferoo.api.UniqueId;
import io.transferoo.resource.TransferooEndpoints;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Function;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import org.junit.Test;

public class TransactionResourceAcceptanceTest extends AcceptanceTestBase {

    private final BigDecimal sourceAccountBalance = new BigDecimal("10.0");
    private final BigDecimal destinationAccountBalance = new BigDecimal("100.0");
    private final AccountMetadata sourceAccountMetadata = AccountMetadata.builder()
                                                                         .balance(sourceAccountBalance)
                                                                         .build();
    private final AccountMetadata destinationAccountMetadata = AccountMetadata.builder()
                                                                              .balance(destinationAccountBalance)
                                                                              .build();

    private Account sourceAccount;
    private Account destinationAccount;

    @Before
    public void before() {
        sourceAccount = createAccount(sourceAccountMetadata);
        destinationAccount = createAccount(destinationAccountMetadata);
    }

    @Test
    public void getTransaction_should_fail_for_unknown_id() {
        UniqueId<Transaction> transactionId = UniqueId.of(UUID.randomUUID());
        expectError(ErrorCode.TransactionNotFound,
                    "Unknown transaction: " + transactionId.id(),
                    getTransactionResponse(transactionId));
    }

    @Test
    public void getTransaction_should_fail_for_incorrectly_formed_uuid() {
        String wrongId = "asd";
        expectError(ErrorCode.InvalidId,
                    "Invalid id value: " + wrongId,
                    target().path(TransferooEndpoints.TRANSACTION_RESOURCE + "/{id}")
                            .resolveTemplate("id", wrongId)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .get());
    }

    @Test
    public void createTransaction_should_fail_if_source_is_same_as_destination() {
        TransactionMetadata metadata = transaction().destination(sourceAccount.id())
                                                    .build();
        expectError(ErrorCode.SourceSameAsDestination,
                    "Source account must not be equal to destination account: " + metadata.source(),
                    tryCreateTransaction(metadata));
    }

    @Test
    public void createTransaction_should_fail_if_transaction_amount_is_zero() {
        TransactionMetadata metadata = transaction().amount(BigDecimal.ZERO)
                                                    .build();
        checkAmountNonPositive(metadata);
    }

    @Test
    public void createTransaction_should_fail_if_transaction_amount_is_negative() {
        TransactionMetadata metadata = transaction().amount(BigDecimal.ONE.negate())
                                                    .build();
        checkAmountNonPositive(metadata);
    }

    @Test
    public void createTransaction_should_fail_for_unknown_source_transaction() {
        UniqueId<Account> accountId = UniqueId.of(UUID.randomUUID());
        expectError(ErrorCode.UnknownTransactionAccountId,
                    "Unknown source account: " + accountId.id().toString(),
                    tryCreateTransaction(transaction().source(accountId).build()));
    }

    @Test
    public void createTransaction_should_fail_for_unknown_destination_transaction() {
        UniqueId<Account> accountId = UniqueId.of(UUID.randomUUID());
        expectError(ErrorCode.UnknownTransactionAccountId,
                    "Unknown destination account: " + accountId.id().toString(),
                    tryCreateTransaction(transaction().destination(accountId).build()));
    }

    @Test
    public void createTransaction_should_fail_if_source_has_insufficient_balance() {
        TransactionMetadata transactionMetadata = transaction().amount(sourceAccountBalance.add(BigDecimal.ONE))
                                                               .build();
        expectError(ErrorCode.InsufficientBalance,
                    "Unsufficient balance: amount="
                  + transactionMetadata.amount()
                  + ", balance=" + sourceAccountBalance,
                    tryCreateTransaction(transactionMetadata));
    }

    @Test
    public void createTransaction_should_succeed_if_source_has_enough_balance() {
        checkTransaction(sourceAccountBalance.subtract(BigDecimal.ONE));
    }

    @Test
    public void createTransaction_should_successfully_transfer_entire_balance() {
        checkTransaction(sourceAccountBalance);
    }

    private void checkAmountNonPositive(TransactionMetadata metadata) {
        expectError(ErrorCode.TransactionAmountNonPositive,
                    "Amount must be greater than zero: amount=" + metadata.amount(),
                    tryCreateTransaction(metadata));
    }

    private void checkTransaction(BigDecimal amount) {
        TransactionMetadata transactionMetadata = transaction().amount(amount)
                                                               .build();

        Transaction transaction = createTransaction(transactionMetadata);
        assertThat(transaction).isEqualTo(getTransaction(transaction.id()));

        assertThat(transaction.metadata()).isEqualTo(transactionMetadata);

        checkBalance(sourceAccount, (originalBalance) -> originalBalance.subtract(amount));
        checkBalance(destinationAccount, (originalBalance) -> originalBalance.add(amount));
    }

    private TransactionMetadata.Builder transaction() {
        return TransactionMetadata.builder()
                                  .source(sourceAccount.id())
                                  .destination(destinationAccount.id())
                                  .amount(sourceAccountBalance);
    }

    private void checkBalance(Account account, Function<BigDecimal, BigDecimal> modifier) {
        Account accountAfterTransfer = getAccount(account.id());
        assertThat(accountAfterTransfer.metadata().balance()).isEqualTo(modifier.apply(account.metadata().balance()));
    }
}
