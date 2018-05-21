package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import com.tzj.parsercombinators.core.Parser;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.parse;
import static org.junit.Assert.assertEquals;

public class AndThenTest {
    @Test
    public void testAndThen() {
        // a parser that extracts the first and third characters
        final Parser<Pair<Character, Character>> three =
                (input) -> {
                    if (input == null || input.length() < 3) {
                        return List.of();
                    }

                    final char x = input.charAt(0);
                    final char z = input.charAt(2);
                    final String rest = input.substring(3, input.length());

                    return List.of(new Pair<>(new Pair<>(x, z), rest));
                };

        // a parser that extracts the first and second characters
        final Parser<Pair<Character, Character>> two =
                (input) -> {
                    if (input == null || input.length() < 2) {
                        return List.of();
                    }

                    final char x = input.charAt(0);
                    final char z = input.charAt(1);
                    final String rest = input.substring(2, input.length());

                    return List.of(new Pair<>(new Pair<>(x, z), rest));
                };

        final String x = "abcdefghi";
        assertEquals(parse(three.andThen(two), x), List.of(new Pair<>(new Pair<>('a', 'c'), "defghi"), new Pair<>(new Pair<>('d', 'e'), "fghi")));
    }
}
