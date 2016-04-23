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

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.transferoo.api.Account;
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.ErrorCode;
import io.transferoo.api.Transaction;
import io.transferoo.api.TransactionMetadata;
import io.transferoo.api.TransferooError;
import io.transferoo.api.UniqueId;
import io.transferoo.resource.TransferooEndpoints;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.ClassRule;

public abstract class AcceptanceTestBase {

    @ClassRule
    public static final DropwizardAppRule<TransferooConfiguration> RULE =
            new DropwizardAppRule<>(TransferooServer.class,
                                    ResourceHelpers.resourceFilePath("transferoo.yml"));

    private static Client c;

    @BeforeClass
    public static void beforeClass() {
        c = new JerseyClientBuilder(RULE.getEnvironment()).build("client");
    }

    protected WebTarget target() {
        return c.target(String.format("http://localhost:%d/api", RULE.getLocalPort()));
    }

    protected Account createAccount(AccountMetadata metadata) {
        Response response = target().path(TransferooEndpoints.ACCOUNT_RESOURCE)
                                    .request()
                                    .post(Entity.entity(metadata, MediaType.APPLICATION_JSON_TYPE));

        Account actualAccount = checkCreated(Account.class, response);
        assertThat(actualAccount.metadata()).isEqualTo(metadata);
        return actualAccount;
    }

    protected Response getAccountResponse(UniqueId<Account> accountId) {
        return target().path(TransferooEndpoints.ACCOUNT_RESOURCE + "/{id}")
                       .resolveTemplate("id", accountId.id().toString())
                       .request(MediaType.APPLICATION_JSON_TYPE)
                       .get();
    }

    protected Account getAccount(UniqueId<Account> accountId) {
        return getAccountResponse(accountId).readEntity(Account.class);
    }

    protected Response getTransactionResponse(UniqueId<Transaction> transactionId) {
        return target().path(TransferooEndpoints.TRANSACTION_RESOURCE + "/{id}")
                       .resolveTemplate("id", transactionId.id().toString())
                       .request(MediaType.APPLICATION_JSON_TYPE)
                       .get();
    }

    protected Transaction getTransaction(UniqueId<Transaction> transactionId) {
        return getTransactionResponse(transactionId).readEntity(Transaction.class);
    }

    protected Transaction createTransaction(TransactionMetadata metadata) {
        Response response = tryCreateTransaction(metadata);
        Transaction actualTransaction = checkCreated(Transaction.class, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
        return actualTransaction;
    }

    protected Response tryCreateTransaction(TransactionMetadata metadata) {
        return target().path(TransferooEndpoints.TRANSACTION_RESOURCE)
                       .request()
                       .post(Entity.entity(metadata, MediaType.APPLICATION_JSON_TYPE));
    }

    protected void expectError(ErrorCode errorCode, String message, Response response) {
        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(errorCode.getStatus().getStatusCode());
        TransferooError expectedError = TransferooError.of(message, errorCode);
        TransferooError actualError = response.readEntity(TransferooError.class);
        assertThat(actualError).isEqualTo(expectedError);
    }

    private <T> T checkCreated(Class<T> clazz, Response response) {
        T actual = response.readEntity(clazz);
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        assertThat(c.target(response.getLocation())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(clazz)).isEqualTo(actual);

        return actual;
    }
}
