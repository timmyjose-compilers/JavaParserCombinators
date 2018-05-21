package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class SymbolTest {
    @Test
    public void testSymbol() {
        final String x = "(abc";
        assertEquals(parse(symbol("("), x), List.of(new Pair<>("(", "abc")));

        final String y = "   (   abc 123";
        assertEquals(parse(symbol("("), y), List.of(new Pair<>("(", "   abc 123")));
    }
}
