package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class SomeTest {
    @Test
    public void main() {
        final String x = "123abc";
        assertEquals(parse(some(digit), x), List.of(new Pair<>("123", "abc")));

        final String y = "abc";
        assertEquals(parse(some(digit), y), List.of());
    }
}
