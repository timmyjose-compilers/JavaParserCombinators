package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import com.tzj.parsercombinators.core.Parser;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.parse;
import static org.junit.Assert.assertEquals;

public class OrElseTest {
    @Test
    public void testOrElse() {
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
        assertEquals(parse(three.orElse(two), x), List.of(new Pair<>(new Pair<>('a', 'c'), "defghi")));

        final String y = "abc";
        assertEquals(parse(three.orElse(two), y), List.of(new Pair<>(new Pair<>('a', 'c'), "")));

        final String z = "ab";
        assertEquals(parse(three.orElse(two), z), List.of(new Pair<>(new Pair<>('a', 'b'), "")));
    }
}
