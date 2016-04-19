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
import io.transferoo.api.CreateAccount;
import io.transferoo.resource.TransferooEndpoints;
import java.math.BigDecimal;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class AccountAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<TransferooConfiguration> RULE =
            new DropwizardAppRule<>(TransferooServer.class,
                    ResourceHelpers.resourceFilePath("transferoo.yml"));
    private static Client c;

    private final CreateAccount createAccount = CreateAccount.builder()
            .balance(new BigDecimal(10.50))
            .build();


    @BeforeClass
    public static void before() {
        c = new JerseyClientBuilder(RULE.getEnvironment()).build("client");
    }

    @Test
    public void createAccount_should_create_account_and_return_uri_to_it() {
        createAccount();
    }

    @Test
    public void getAccount_should_lookup_account_by_id() {
        Account account = createAccount();
        Account actualAccount = target().path("accounts/{id}")
                                   .resolveTemplate("id", account.id().id().toString())
                                   .request(MediaType.APPLICATION_JSON_TYPE)
                                   .get(Account.class);
        assertThat(actualAccount).isEqualTo(account);
    }

    private Account createAccount() {
        Response response = target().path(TransferooEndpoints.ACCOUNT_RESOURCE)
                .request()
                .post(Entity.entity(createAccount, MediaType.APPLICATION_JSON_TYPE));

        Account actualAccount = response.readEntity(Account.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);
        assertThat(actualAccount.balance()).isEqualTo(createAccount.balance());

        assertThat(c.target(response.getLocation())
           .request(MediaType.APPLICATION_JSON_TYPE)
           .get(Account.class)).isEqualTo(actualAccount);

        return actualAccount;
    }



    private WebTarget target() {
        return c.target(String.format("http://localhost:%d/api", RULE.getLocalPort()));
    }
}
