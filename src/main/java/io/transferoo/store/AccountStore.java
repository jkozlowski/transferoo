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
import io.transferoo.api.AccountId;
import io.transferoo.api.CreateAccount;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * All methods are globally synchronized: not ideal, but this is the simplest implementation.
 */
public class AccountStore {

    private static final int MAX_RETRY = 5;
    private final Map<AccountId, Account> accounts = new HashMap<>();

    public Account createAccount(CreateAccount createAccount) {
        AccountId accountId = generateAccountId();
        Account account = Account.builder()
                                 .id(accountId)
                                 .balance(createAccount.balance())
                                 .build();
        accounts.put(accountId, account);
        return account;
    }

    public synchronized Optional<Account> getAccountById(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    private AccountId generateAccountId() {
        for (int i = 0; i < MAX_RETRY; i++) {
            AccountId candidate = AccountId.of(UUID.randomUUID());
            if (!accounts.containsKey(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique account id!");
    }
}
