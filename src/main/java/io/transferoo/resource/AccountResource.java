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
import io.transferoo.api.AccountMetadata;
import io.transferoo.api.ErrorCode;
import io.transferoo.api.UniqueId;
import io.transferoo.store.AccountStore;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(TransferooEndpoints.ACCOUNT_RESOURCE)
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
    public Account getAccount(@NotNull @PathParam("id") UniqueId<Account> id) {
        return accounts.getAccountById(id)
                       .orElseThrow(ErrorCode.accountNotFound(id));
    }

    @POST
    @Timed
    public Response createAccount(@NotNull @Valid AccountMetadata metadata) {
        Account account = accounts.createAccount(metadata);
        return TransferooEndpoints.createdResponse(uri, account);
    }
}
