package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class IntegerTest {
    @Test
    public void testInteger() {
        final String v = "   -123 abc ";
        assertEquals(parse(integer, v), List.of(new Pair<>(-123, " abc")));

        final String w = "   123 abc";
        assertEquals(parse(integer, w), List.of(new Pair<>(123, " abc")));

        final String x = "-123 abc";
        assertEquals(parse(integer, x), List.of(new Pair<>(-123, " abc")));

        final String y = "123 abc";
        assertEquals(parse(integer, y), List.of(new Pair<>(123, " abc")));

        final String z = "abc 123";
        assertEquals(parse(integer, z), List.of());
    }
}
