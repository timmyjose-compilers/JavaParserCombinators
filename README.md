# An experimental Parser Combinator framework in Java

This is an experiment in implementing a simple and Functional Parser Combinator framework
in Java, inspired by the wonderful Haskell examples in the book, "Programming in Haskell"
(2nd Edition) by Professor Graham Hutton.

Due to the constraints of the Java Generics system, as well as an absolute lack of any
real metaprogramming capability in Java, the implementation is a lot more verbose and
klunky than the equivalent would be in, say, Haskell or F#. In fact, Haskell's Monads
would allow for a very nifty and clean `do` syntax for all the parsers and combinators.

That being said, the idea is very simple - use the available lambda support in Java 9
(and above) to build up parsers from scratch - using simple parsers as the base and then
using combinators (`andThen` and `orElse` are the two basic combinators provided) to create
more complex parsers.

This undertaking also helped solidify my understanding of the way Monads work in dealing
with Effectful Programming, especially when it comes to handling the error-checking in
a single place, and propagating effects through the pipeline of computations.


### Prerequisites

You will need JDK 9 (or above) in order to run the project. Also, `maven` is used as the
build tool for this project.


### Installing

Clone or fork the project, compile it, and then run the tests.

```
Macushla:ParserCombinators z0ltan$ mvn clean && mvn compile && mvn test
[INFO] Scanning for projects...
[INFO] Building parser-combinators 1.0
[INFO] ------------------------------------------------------------------------

(content elided)

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.tzj.parsercombinators.IntegerTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.107 sec
Running com.tzj.parsercombinators.ManyTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec
Running com.tzj.parsercombinators.SpaceTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 sec
Running com.tzj.parsercombinators.LowerTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.StringTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.SymbolTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.DigitTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.SomeTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.AndThenTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.OrElseTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.CharTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec
Running com.tzj.parsercombinators.IdentTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running com.tzj.parsercombinators.ExprTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 sec

Results :

Tests run: 13, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.342 s
[INFO] Finished at: 2018-05-21T22:02:05+05:30
[INFO] Final Memory: 13M/47M
[INFO] ------------------------------------------------------------------------
```

## Sample Parsers

A look at the test file, `ExprTest` is recommended. Here, using the basic parsers, and 
the provided combinators, we build up a parsing framework for simple arithmetic expressions
supporting addition, subtraction, and multiplication.

For instance, the `expr` parser is created as shown below:
```
        expr =
                (input) -> {
                    final List&lt;Pair&lt;Integer, String&gt;&gt; res0 = parse(term, input);
                    if (res0 == null || res0.isEmpty()) {
                        throw new IllegalStateException("expected term");
                    }

                    final Integer value = res0.get(0).first();
                    final String rest = res0.get(0).second();
                    final List&lt;Pair&lt;String, String&gt;&gt; res1 = parse(symbol("+"), res0.get(0).second());

                    if (res1 == null || res1.isEmpty()) {
                        return List.of(new Pair<>(value, rest));
                    }

                    final List<Pair<Integer, String>> res2 = parse(expr, res1.get(0).second());
                    if (res2 == null || res2.isEmpty()) {
                        throw new IllegalStateException("expected expression");
                    }

                    return List.of(new Pair<>(value + res2.get(0).first(), res2.get(0).second()));
                };
```

Here, `parse` is the common parsing method that works with any parser. The `symbol` parser
simply matches against the provided string, taking care of whitespace.

So now, it can evaluate expressions such as `(2 + 3) * (4 + (5 * 10))` et al. 

Another simple parser is the `many` parser that matches the supplied parser zero or
more times:

```
    // this parser matches zero or more occurrences of the supplied
    // parser
    public static Parser<String> many(final Parser<String> parser) {
        return (input) -> {
            List&lt;Pair&lt;String, String&gt;&gt; res = List.of(new Pair<>("", input));
            List&lt;Pair&lt;String, String&gt;&gt; res1 = null;
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
```

For an exhaustive list of the provided parsers, check the `Parsers.java` file. 

For a final example, here is an example of how the` orElse` combinator works:

```
    @Test
    public void testOrElse() {
        // a parser that extracts the first and third characters
        final Parser&lt;Pair&lt;Character, Character&gt;&gt; three =
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
        final Parser&lt;Pair&lt;Character, Character&gt;&gt; two =
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
```

And finally, here is how the `orElse` combinator itself is defined (in the `Parser` interface) - 
it simply tries the first parser, and upon failure, tries the second parser. Of courses, these could be
chained together to arbitrary lengths, provided the types match up:

```

    // try the first parser, and if it fails, try the second one.
    // this can be chained arbitrarily long.
    default Parser&lt;P&gt; orElse(final Parser&lt;P&gt; otherParser) {
        return (input) -> {
            final List<Pair<P, String>> res = parse(input);
            if (res == null || res.isEmpty()) {
                return otherParser.parse(input);
            }

            return res;
        };

```

Simplicity itself!

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/timmyjose/JavaParserCombinators/blob/master/LICENSE) file for details

