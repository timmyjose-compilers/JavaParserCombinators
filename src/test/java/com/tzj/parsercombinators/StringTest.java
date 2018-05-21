package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class StringTest {
    @Test
    public void testString() {
        final String x = "hello, world!";
        assertEquals(parse(string("hello"), x), List.of(new Pair<>("hello", ", world!")));

        final String y = "Hello, world!";
        assertEquals(parse(string("hello"), y), List.of());
    }
}
