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

import io.transferoo.api.Account;
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.UniqueId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * All methods are globally synchronized: not ideal, but this is the simplest implementation.
 */
public class AccountStore {

    private static final int MAX_RETRY = 5;
    private final Map<UniqueId<Account>, Account> accounts = new HashMap<>();

    public Account createAccount(AccountMetadata metadata) {
        UniqueId<Account> uniqueId = generateAccountId();
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

    // Ideally, we'd have a globally unique id implementation handy,
    // but UUID is type 4.
    private UniqueId<Account> generateAccountId() {
        for (int i = 0; i < MAX_RETRY; i++) {
            UniqueId<Account> candidate = UniqueId.of(UUID.randomUUID());
            if (!accounts.containsKey(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique id!");
    }
}
