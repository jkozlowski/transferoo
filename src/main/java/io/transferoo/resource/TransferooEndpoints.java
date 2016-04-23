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

import io.transferoo.api.HasUniqueId;
import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface TransferooEndpoints {

    String ACCOUNT_RESOURCE = "accounts";
    String TRANSACTION_RESOURCE = "transactions";
    String GET_BY_ID_PATH = "{id}";

    static Response createdResponse(UriInfo uri, HasUniqueId<?> resource) {
        return Response.created(resourceUri(uri, resource))
                       .entity(resource)
                       .build();
    }

    static URI resourceUri(UriInfo uri, HasUniqueId<?> resource) {
        return uri.getAbsolutePathBuilder()
                  .path(GET_BY_ID_PATH)
                  .build(resource.id().id().toString());
    }
}
