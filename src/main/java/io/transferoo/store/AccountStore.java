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
import io.transferoo.api.Transaction;
import io.transferoo.api.TransactionMetadata;
import io.transferoo.api.UniqueId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.concurrent.ThreadSafe;

/**
 * All methods are globally synchronized: not ideal, but this is the simplest implementation.
 */
@ThreadSafe
public class AccountStore {

    private final Map<UniqueId<Account>, Account> accounts = new HashMap<>();

    public Account createAccount(AccountMetadata metadata) {
        UniqueId<Account> uniqueId = UniqueId.of(UUID.randomUUID());
        Preconditions.checkState(!accounts.containsKey(uniqueId),
                                 "Oh noes, UUIDs just clashed! Lucky you! Try again later!");
        Account account = Account.builder()
                                 .id(uniqueId)
                                 .metadata(metadata)
                                 .build();
        accounts.put(uniqueId, account);
        return account;
    }

    public synchronized Optional<Account> getAccountById(UniqueId<Account> uniqueId) {
        return Optional.ofNullable(accounts.get(uniqueId));
    }

    public synchronized Transaction createTransaction(TransactionMetadata metadata) {
        getAccountById(metadata.source())
                .orElseThrow(() -> new IllegalStateException("Unknown account: " + metadata.source()
                        .id().toString()));
        return null;
    }

    public synchronized Optional<Transaction> getTransactionById(UniqueId<Transaction> uniqueId) {
        return Optional.empty();
    }
}
