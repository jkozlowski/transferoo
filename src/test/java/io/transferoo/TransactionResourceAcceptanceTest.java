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

import io.transferoo.api.Account;
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.TransactionMetadata;
import io.transferoo.api.UniqueId;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class TransactionResourceAcceptanceTest extends AcceptanceTestBase {

    private final BigDecimal account1Balance = new BigDecimal("10.0");
    private final BigDecimal account2Balance = new BigDecimal("100.0");
    private final AccountMetadata account1Metadata = AccountMetadata.builder()
                                                                    .balance(account1Balance)
                                                                    .build();
    private final AccountMetadata account2Metadata = AccountMetadata.builder()
                                                                    .balance(account2Balance)
                                                                    .build();

    private Account account1;
    private Account account2;

    @Before
    public void before() {
        account1 = createAccount(account1Metadata);
        account2 = createAccount(account2Metadata);
    }

    @Test
    public void createTransaction_should_fail_for_unknown_source_transaction() {
        tryCreateTransaction(account1ToAccount2().source(UniqueId.of(UUID.randomUUID())).build());
    }

    private TransactionMetadata.Builder account1ToAccount2() {
        return TransactionMetadata.builder()
                                  .source(account1.id())
                                  .destination(account2.id())
                                  .amount(account1Balance);
    }
}
