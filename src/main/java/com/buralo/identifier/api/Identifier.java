package com.buralo.identifier.api;

import java.util.Arrays;
import java.util.Objects;

public record Identifier(String text, byte[] binary) implements Comparable<Identifier> {

    @Override
    public boolean equals(final Object other) {
        return (this == other) || (other instanceof Identifier that && text.equals(that.text) && Arrays.equals(binary, that.binary));
    }

    @Override
    public int hashCode() {
        return Objects.hash(text) * 31 + Arrays.hashCode(binary);
    }

    @Override
    public int compareTo(final Identifier other) {
        return Arrays.compare(binary, other.binary);
    }
}
