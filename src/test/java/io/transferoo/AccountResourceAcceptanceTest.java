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
import io.transferoo.api.UniqueId;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;

public class AccountResourceAcceptanceTest extends AcceptanceTestBase {

    private final AccountMetadata accountMetadata = AccountMetadata.builder()
                                                                   .balance(new BigDecimal(10.50))
                                                                   .build();

    @Test
    public void createAccount_should_create_account_and_return_uri_to_it() {
        createAccount(accountMetadata);
    }

    @Test
    public void createAccount_should_allow_negative_balance() {
        createAccount(accountMetadata.toBuilder()
                                     .balance(new BigDecimal("-1.0"))
                                     .build());
    }

    @Test
    public void getAccount_should_lookup_account_by_id() {
        Account createAccount = createAccount(accountMetadata);
        Account actualAccount = getAccount(createAccount.id());
        assertThat(actualAccount).isEqualTo(createAccount);
    }

    @Test
    public void getAccount_should_fail_for_unknown_id() {
        UniqueId<Account> of = UniqueId.of(UUID.randomUUID());
        expectError(ErrorCode.accountNotFound(of), getAccountResponse(of));
    }
}
