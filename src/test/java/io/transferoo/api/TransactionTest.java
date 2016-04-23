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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.FixtureHelpers;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TransactionTest {

    private static final UniqueId<Transaction> ID = UniqueId.valueOf("d3c02886-2c36-450c-86cf-e199b3ecd330");
    private static final UniqueId<Account> SOURCE = UniqueId.valueOf("d3c02886-2c36-450c-86cf-e199b3ecd333");
    private static final UniqueId<Account> DESTINATION = UniqueId.valueOf("d3c02886-2c36-450c-86cf-e199b3ecd331");
    private static final Transaction EXPECTED = Transaction.builder()
            .id(ID)
            .metadata(TransactionMetadata.builder()
                                         .source(SOURCE)
                                         .destination(DESTINATION)
                                         .amount(new BigDecimal("10.23"))
                                         .build())
            .build();
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private static final String FIXTURE = "fixtures/Transaction.json";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void should_serialize_cleanly() throws Exception {
        final String expected = MAPPER.writeValueAsString(readCreateTransaction());
        assertThat(MAPPER.writeValueAsString(EXPECTED)).isEqualTo(expected);
    }

    @Test
    public void should_deserialize_cleanly() throws IOException {
        Transaction actual = readCreateTransaction();
        assertThat(actual).isEqualTo(EXPECTED);
    }

    private Transaction readCreateTransaction() throws IOException {
        return MAPPER.readValue(FixtureHelpers.fixture(FIXTURE), Transaction.class);
    }
}
