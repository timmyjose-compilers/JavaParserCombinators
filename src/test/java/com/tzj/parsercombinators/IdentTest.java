package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class IdentTest {
    @Test
    public void testIdent() {
        final String x = "abc def";
        assertEquals(parse(identifier, x), List.of(new Pair<>("abc", " def")));

        final String y = "123 abc";
        assertEquals(parse(identifier, y), List.of());

        final String z = "   abc 123";
        assertEquals(parse(identifier, z), List.of(new Pair<>("abc", " 123")));
    }
}
