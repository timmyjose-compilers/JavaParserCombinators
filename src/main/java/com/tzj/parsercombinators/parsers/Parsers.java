package com.tzj.parsercombinators.parsers;

import com.tzj.parsercombinators.core.Pair;
import com.tzj.parsercombinators.core.Parser;

import java.util.List;
import java.util.function.Predicate;

/**
 * Some primitive parsers. The behaviour is indicated this -
 * if the return value is an empty list, that indicates failure.
 * Otherwise, the result is a tuple - the first value representing the
 * result of the parse, and the second value the remaining part of the
 * input string.
 * <p>
 * Using `andThen` and `orElse`, more complex parsers can be chained together,
 * however ensuring that the types match up properly. The Java type system is
 * not as robust as that of, say, Haskell, and so a lot of constraints are
 * inherently part of the system, especially when it comes to Generics.
 */
public class Parsers {
    public static Parser<Character> item =
            (input) -> {
                if (input == null || input.length() == 0) {
                    return List.of();
                }

                final Character c = input.charAt(0);
                final String rest = input.substring(1, input.length());

                return List.of(new Pair(c, rest));
            };

    // note that the type is a String for all these parsers, look at the
    // type system comment above
    public static Parser<String> digit = satisfies(Character::isDigit);

    public static Parser<String> lower = satisfies(Character::isLowerCase);

    public static Parser<String> upper = satisfies(Character::isUpperCase);

    public static Parser<String> letter = satisfies(Character::isLetter);

    public static Parser<String> alphanum = satisfies(Character::isLetterOrDigit);

    public static Parser<String> space =
            (input) -> {
                final List<Pair<String, String>> res = parse(many(satisfies(Character::isWhitespace)), input);
                if (res == null || res.isEmpty()) {
                    return List.of(new Pair<>("", input));
                } else {
                    return List.of(new Pair<>("", res.get(0).second().trim()));
                }
            };

    // match a natural number
    public static Parser<Integer> nat =
            (input) -> {
                String inputCopy = null;
                final List<Pair<String, String>> res0 = parse(space, input);
                if (res0 == null || res0.isEmpty()) {
                    inputCopy = input;
                } else {
                    inputCopy = res0.get(0).second();
                }

                final List<Pair<String, String>> res = parse(some(digit), inputCopy);
                if (res == null || res.isEmpty()) {
                    return List.of();
                }

                return List.of(new Pair<>(Integer.parseInt(res.get(0).first()), res.get(0).second()));
            };

    // match an identifier - a letter followed by
    // any number of alphanumeric characters
    public static Parser<String> identifier =
            (input) -> {
                String inputCopy = null;
                final List<Pair<String, String>> res0 = parse(space, input);
                if (res0 == null || res0.isEmpty()) {
                    inputCopy = input;
                } else {
                    inputCopy = res0.get(0).second();
                }

                final List<Pair<String, String>> res1 = parse(lower, inputCopy);
                if (res1 == null || res1.isEmpty()) {
                    return List.of();
                }

                return parse(many(alphanum), inputCopy);
            };

    // match an integer - a natural number with an optional
    // sign
    public static Parser<Integer> integer =
            (input) -> {
                String inputCopy = null;
                final List<Pair<String, String>> res0 = parse(space, input);
                if (res0 == null || res0.isEmpty()) {
                    inputCopy = input;
                } else {
                    inputCopy = res0.get(0).second();
                }

                final List<Pair<String, String>> res = parse(character('-'), inputCopy);
                if (res == null || res.isEmpty()) {
                    return parse(nat, inputCopy);
                }

                final List<Pair<Integer, String>> res1 = parse(nat, res.get(0).second());

                return List.of(new Pair<>(-res1.get(0).first(), res1.get(0).second()));
            };

    // match the supplied character exactly
    public static Parser<String> character(final char c) {
        return (input) -> {
            if (input == null || input.isEmpty()) {
                return List.of();
            }

            return parse(satisfies((x) -> x == c), input);
        };
    }

    // match the supplied string exactly
    public static Parser<String> string(final String s) {
        return (input) -> {
            final char[] cs = s.toCharArray();

            List<Pair<String, String>> res = null;
            String inputCopy = input;
            for (final char c : cs) {
                res = parse(character(c), inputCopy);
                if (res == null || res.isEmpty()) {
                    return List.of();
                }
                inputCopy = res.get(0).second();
            }

            return List.of(new Pair<>(s, res.get(0).second()));
        };
    }

    // check if the given character satisfies the predicate, and if so,
    // return a Parser of String (again, to satisfy the type system).
    private static Parser<String> satisfies(Predicate<Character> pred) {
        return (input) -> {
            if (input == null || input.isEmpty()) {
                return List.of();
            }

            final Character first = input.charAt(0);
            if (!pred.test(first)) {
                return List.of();
            }

            final String rest = input.substring(1, input.length());

            return List.of(new Pair<>(String.valueOf(first), rest));
        };
    }

    // this parser matches zero or more occurrences of the supplied
    // parser
    public static Parser<String> many(final Parser<String> parser) {
        return (input) -> {
            List<Pair<String, String>> res = List.of(new Pair<>("", input));
            List<Pair<String, String>> res1 = null;
            String inputCopy = input;

            while (true) {
                res1 = parser.parse(inputCopy);
                if (res1 == null || res1.isEmpty()) {
                    break;
                }
                inputCopy = res1.get(0).second();
                if (res == null || res.isEmpty()) {
                    res = List.of(new Pair<>(res1.get(0).first(), res1.get(0).second()));
                } else {
                    res = List.of(new Pair<>(res.get(0).first() + res1.get(0).first(), res1.get(0).second()));
                }
            }

            return res;
        };
    }

    // this parser matches one or more occurrences of the given
    // parser
    public static Parser<String> some(final Parser<String> parser) {
        return (input) -> {
            List<Pair<String, String>> res = parser.parse(input);
            if (res == null || res.isEmpty()) {
                return List.of();
            }

            List<Pair<String, String>> res1 = null;
            String inputCopy = res.get(0).second();

            while (true) {
                res1 = parser.parse(inputCopy);
                if (res1 == null || res1.isEmpty()) {
                    break;
                }
                inputCopy = res1.get(0).second();
                if (res == null || res.isEmpty()) {
                    res = List.of(new Pair<>(res1.get(0).first(), res1.get(0).second()));
                } else {
                    res = List.of(new Pair<>(res.get(0).first() + res1.get(0).first(), res1.get(0).second()));
                }
            }

            return res;
        };
    }

    // given a string, return a Parser that matches this
    // exact string, allowing for whitespace
    public static Parser<String> symbol(final String s) {
        return (input) -> {
            String inputCopy = null;
            final List<Pair<String, String>> res0 = parse(space, input);
            if (res0 == null || res0.isEmpty()) {
                inputCopy = input;
            } else {
                inputCopy = res0.get(0).second();
            }

            return parse(string(s), inputCopy);
        };
    }

    // the standalone common parser method.
    public static <P> List<Pair<P, String>> parse(final Parser<P> parser, final String input) {
        return parser.parse(input);
    }
}
