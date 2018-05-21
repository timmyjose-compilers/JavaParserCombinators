package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class CharTest {
    @Test
    public void testChar() {
        final String x = "abc";
        assertEquals(parse(character('a'), x), List.of(new Pair<>("a", "bc")));

        final String y = "bcdef";
        assertEquals(parse(character('a'), y), List.of());
    }
}
