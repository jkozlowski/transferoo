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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * Single page of results.
 */
public final class Page<TypeT, TokenT> {

    private final ImmutableList<TypeT> values;
    private final Optional<TokenT> nextPageToken;

    @JsonCreator
    private Page(@JsonProperty("values") ImmutableList<TypeT> values,
                 @JsonProperty("nextPageToken") Optional<TokenT> nextPageToken) {
        this.values = Preconditions.checkNotNull(values, "values");
        this.nextPageToken = Preconditions.checkNotNull(nextPageToken, "nextPageToken");
    }

    @Value.Parameter
    public List<TypeT> values() {
        return values;
    }

    @Value.Parameter
    public Optional<TokenT> nextPageToken() {
        return nextPageToken;
    }

    public static <TypeT, TokenT> Page<TypeT, TokenT> of(Iterable<TypeT> values,
                                                     Optional<TokenT> nextPageToken) {
        return new Page<>(ImmutableList.copyOf(values), nextPageToken);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Page<?, ?> page = (Page<?, ?>) other;
        return Objects.equals(values, page.values)
               && Objects.equals(nextPageToken, page.nextPageToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, nextPageToken);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("values", values)
                          .add("nextPageToken", nextPageToken)
                          .toString();
    }
}
