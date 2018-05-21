package com.tzj.parsercombinators;

import com.tzj.parsercombinators.core.Pair;
import com.tzj.parsercombinators.core.Parser;
import org.junit.Test;

import java.util.List;

import static com.tzj.parsercombinators.parsers.Parsers.*;
import static org.junit.Assert.assertEquals;

public class ExprTest {
    //  forward declaration required here
    private static Parser<Integer> expr;
    private static Parser<Integer> term;
    private static Parser<Integer> factor;

    private static int eval(final String input) {
        final List<Pair<Integer, String>> res = parse(expr, input);
        if (res == null || res.isEmpty()) {
            throw new IllegalStateException("Invalid expression");
        }

        final int value = res.get(0).first();
        final String unconsumed = res.get(0).second();

        if (unconsumed != null && !unconsumed.isEmpty()) {
            throw new IllegalStateException("Unused input: " + unconsumed);
        }

        return value;
    }

    @Test
    public void testExpr() {
        initParsers();

        final String u = "(2 + 3) * ((3+4)*2)";
        assertEquals(eval(u), 70);

        final String v = "(2 * 3) * (5 + 4)";
        assertEquals(eval(v), 54);

        final String w = " 2 * ( 3 + 4 )";
        assertEquals(eval(w), 14);

        final String x = "12*3";
        assertEquals(eval(x), 36);

        final String y = "2 * 13 + (4)";
        assertEquals(eval(y), 30);

        final String z = " 2 + 3 + 4 ";
        assertEquals(eval(z), 9);
    }

    /**
     * Expr ::= Expr ("+" Term | epsilon)
     * Term ::= Term ("*" Factor | epsilon)
     * Factor ::= "(" Expr ")" | Expr
     * Terminal ::= [0-9]
     */
    private void initParsers() {
        expr =
                (input) -> {
                    final List<Pair<Integer, String>> res0 = parse(term, input);
                    if (res0 == null || res0.isEmpty()) {
                        throw new IllegalStateException("expected term");
                    }

                    final Integer value = res0.get(0).first();
                    final String rest = res0.get(0).second();
                    final List<Pair<String, String>> res1 = parse(symbol("+"), res0.get(0).second());

                    if (res1 == null || res1.isEmpty()) {
                        return List.of(new Pair<>(value, rest));
                    }

                    final List<Pair<Integer, String>> res2 = parse(expr, res1.get(0).second());
                    if (res2 == null || res2.isEmpty()) {
                        throw new IllegalStateException("expected expression");
                    }

                    return List.of(new Pair<>(value + res2.get(0).first(), res2.get(0).second()));
                };

        factor = (input) -> {
            final List<Pair<String, String>> res0 = parse(symbol("("), input);
            if (res0 == null || res0.isEmpty()) {
                return parse(nat, input);
            }

            final List<Pair<Integer, String>> res1 = parse(expr, res0.get(0).second());
            if (res1 == null || res1.isEmpty()) {
                throw new IllegalStateException("expected expr");
            }

            final List<Pair<String, String>> res2 = parse(symbol(")"), res1.get(0).second());
            if (res2 == null || res2.isEmpty()) {
                throw new IllegalStateException("expected )");
            }

            return List.of(new Pair<>(res1.get(0).first(), res2.get(0).second()));

        };

        term = (input) ->
        {
            final List<Pair<Integer, String>> res0 = parse(factor, input);
            if (res0 == null || res0.isEmpty()) {
                throw new IllegalStateException("expected term");
            }

            final Integer value = res0.get(0).first();
            final String rest = res0.get(0).second();
            final List<Pair<String, String>> res1 = parse(symbol("*"), res0.get(0).second());

            if (res1 == null || res1.isEmpty()) {
                return List.of(new Pair<>(value, rest));
            }

            final List<Pair<Integer, String>> res2 = parse(term, res1.get(0).second());
            if (res2 == null || res2.isEmpty()) {
                throw new IllegalStateException("expected term");
            }

            return List.of(new Pair<>(value * res2.get(0).first(), res2.get(0).second()));
        };
    }
}
