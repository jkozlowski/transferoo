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

package io.transferoo.api;

import java.util.function.Supplier;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public enum ErrorCode {

    // Specifying the error codes by hand
    // in order to not change the ordering by mistake.
    AccountNotFound(Response.Status.NOT_FOUND),
    UnknownTransactionAccountId(Response.Status.BAD_REQUEST),
    TransactionNotFound(Response.Status.NOT_FOUND),
    InsufficientBalance(Response.Status.BAD_REQUEST);

    private final Response.Status status;

    ErrorCode(Response.Status status) {
        this.status = status;
    }

    public static Supplier<WebApplicationException> accountNotFound(UniqueId<Account> accountId) {
        return exception(ErrorCode.AccountNotFound, () -> "Account not found: " + accountId.id().toString());
    }

    public static Supplier<WebApplicationException> unknownAccountId(UniqueId<Account> accountId,
                                                                     TransactionAccountType subtype) {
        return exception(ErrorCode.UnknownTransactionAccountId, () -> "Unknown " + subtype.type()
                                                                    + ": " + accountId.id().toString());
    }

    public static Supplier<WebApplicationException> unknownTransactionId(UniqueId<Transaction> transactionId) {
        return exception(ErrorCode.TransactionNotFound, () -> "Unknown transaction: " + transactionId.id().toString());
    }

    public static Supplier<WebApplicationException> insufficientBalance(TransactionMetadata transactionMetadata,
                                                                        Account source) {
        return exception(ErrorCode.TransactionNotFound, () -> "Unsufficient balance: amount="
                                                            + transactionMetadata.amount()
                                                            + ", balance=" + source.metadata().balance());
    }

    public static WebApplicationException insufficientBalanceException(TransactionMetadata transactionMetadata,
                                                                       Account source) {
        throw exception(ErrorCode.TransactionNotFound, () -> "Unsufficient balance: amount="
                      + transactionMetadata.amount()
                      + ", balance=" + source.metadata().balance()).get();
    }

    private static Supplier<WebApplicationException> exception(ErrorCode errorCode, Supplier<String> message) {
        return () -> {
            TransferooError error = TransferooError.of(message.get(), errorCode);
            Response response = Response.status(errorCode.status)
                                        .entity(error)
                                        .build();
            return new WebApplicationException(response);
        };
    }
}
