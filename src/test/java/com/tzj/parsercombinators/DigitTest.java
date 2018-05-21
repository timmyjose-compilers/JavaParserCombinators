package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class DigitTest {
    @Test
    public void testDigit() {
        final String x = "123abc";
        assertEquals(parse(digit, x), List.of(new Pair<>("1", "23abc")));

        final String y = "abc";
        assertEquals(parse(digit, y), List.of());
    }
}
