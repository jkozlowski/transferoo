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

import com.codahale.metrics.annotation.Timed;
import io.transferoo.api.Account;
import io.transferoo.api.AccountId;
import io.transferoo.api.CreateAccount;
import io.transferoo.store.AccountStore;
import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(TransferooEndpoints.ACCOUNT_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Context
    private UriInfo uri;

    private final AccountStore accounts;

    public AccountResource(AccountStore accounts) {
        this.accounts = Objects.requireNonNull(accounts, "accounts");
    }

    @GET
    @Timed
    @Path("{id}")
    public Account getAccount(@NotNull @PathParam("id") AccountId id) {
        return accounts.getAccountById(id)
                       .orElseThrow(notFound(() -> "Unknown account: " + id));
    }

    @POST
    @Timed
    public Response createAccount(@NotNull @Valid CreateAccount createAccount)
      throws NoSuchMethodException {
        Account account = accounts.createAccount(createAccount);
        return Response.created(accountUri(account))
                       .entity(account)
                       .build();
    }

    private Supplier<WebApplicationException> notFound(Supplier<String> msg) {
        return () -> new WebApplicationException(msg.get(),
                                                 Response.Status.NOT_FOUND);
    }

    private URI accountUri(Account account) throws NoSuchMethodException {
        return TransferooEndpoints.accountUri(uri, account);
    }
}
