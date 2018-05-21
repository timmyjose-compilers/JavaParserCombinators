package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class LowerTest {
    @Test
    public void main() {
        final String x = "abcDEF123";
        assertEquals(parse(lower, x), List.of(new Pair<>("a", "bcDEF123")));

        final String y = "Abc";
        assertEquals(parse(lower, y), List.of());
    }
}
