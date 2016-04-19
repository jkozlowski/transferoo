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

package io.transferoo.resource;

import io.transferoo.api.Account;
import io.transferoo.api.Transaction;
import io.transferoo.api.UniqueId;
import java.net.URI;
import java.util.function.Supplier;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface TransferooEndpoints {

    String ACCOUNT_RESOURCE = "accounts";
    String TRANSACTION_RESOURCE = "transactions";

    static Supplier<WebApplicationException> notFound(Supplier<String> msg) {
        return () -> new WebApplicationException(msg.get(), Response.Status.NOT_FOUND);
    }

    static URI accountUri(UriInfo uri, Account account) throws NoSuchMethodException {
        return uri.getAbsolutePathBuilder()
                  .path(AccountResource.class.getMethod("getAccount", UniqueId.class))
                  .build(account.id().id().toString());
    }

    static URI transactionUri(UriInfo uri, Transaction transaction) throws NoSuchMethodException {
        return uri.getAbsolutePathBuilder()
                .path(TransactionResource.class.getMethod("getTransaction", UniqueId.class))
                .build(transaction.id().id().toString());
    }
}
