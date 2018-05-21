package com.tzj.parsercombinators.core;

import java.util.List;

/**
 * This Functional Interface represents the functionality of a
 * Parser.
 *
 * @param <P>
 */
@FunctionalInterface
public interface Parser<P> {
    List<Pair<P, String>> parse(final String input);

    // try the first parser, and if it fails, try the second one.
    // this can be chained arbitrarily long.
    default Parser<P> orElse(final Parser<P> otherParser) {
        return (input) -> {
            final List<Pair<P, String>> res = parse(input);
            if (res == null || res.isEmpty()) {
                return otherParser.parse(input);
            }

            return res;
        };
    }

    // sequencing parsers - try the first one, and then try
    // the second parser.
    default Parser<P> andThen(final Parser<P> nextParser) {
        return (input) -> {
            final List<Pair<P, String>> res1 = parse(input);
            if (res1 == null || res1.isEmpty()) {
                return List.of();
            }

            final List<Pair<P, String>> res2 = nextParser.parse(res1.get(0).second);
            if (res2 == null || res2.isEmpty()) {
                return List.of();
            }

            return List.of(res1.get(0), res2.get(0));
        };
    }
}
