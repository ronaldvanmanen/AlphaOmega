// Alpha Omega
//
// Copyright (C) 2024 Ronald van Manen <rvanmanen@gmail.com>
//
// This software is provided 'as-is', without any express or implied
// warranty.  In no event will the authors be held liable for any damages
// arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely, subject to the following restrictions:
//
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
// 3. This notice may not be removed or altered from any source distribution.
package com.ragnvaldr.alphaomega.parsing;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ragnvaldr.alphaomega.util.Either;
import com.ragnvaldr.alphaomega.util.Pair;
import com.ragnvaldr.alphaomega.util.Triple;

/**
 * The {@link Parse} class consists exclusively of static factory methods that can be used to create and combine parsers.
 */
public final class Parse {

    private Parse() {}

    /**
     * Returns a parser that matches {@code character}.
     *
     * @param character The character to match.
     *
     * @return A parser that matches {@code character}.
     */
    public static Parser<Character> literal(char character) {
        return new CharacterParser((c) -> c == character);
    }

    /**
     * Returns a parser that matches {@code sequence}.
     *
     * @param sequence The character sequence to match.
     *
     * @return A parser that matches {@code sequence}.
     */
    public static Parser<CharSequence> literal(CharSequence sequence) {
        return new CharacterSequenceParser(sequence);
    }

    /**
     * Returns a parser that matches a range of characters.
     *
     * @param firstCharacter The first character in the range.
     * @param firstCharacter The last character in the range.
     *
     * @return A parser that matches the range [{@code firstCharacter}, {@code lastCharacter}].
     */
    public static Parser<Character> range(char firstCharacter, char lastCharacter) {
        return new CharacterParser((character) -> character >= firstCharacter && character <= lastCharacter);
    }

    /**
     * Returns a parser that matches any of the specified characters.
     *
     * @param characters A set of characters.
     *
     * @return A parser that matches any of the specified characters.
     */
    public static Parser<Character> any(Character... characters) {
        return any(Set.of(characters));
    }

    private static Parser<Character> any(Set<Character> characters) {
        return new CharacterParser((c) -> characters.contains(c));
    }

    /**
     * Returns a parser that matches none of the specified characters.
     *
     * @param characters A set of characters.
     *
     * @return A parser that matches none of the specified characters.
     */
    public static Parser<Character> except(Character... characters) {
        return except(Set.of(characters));
    }

    private static Parser<Character> except(Set<Character> characters) {
        return new CharacterParser((c) -> !characters.contains(c));
    }

    /**
     * Returns a parser that matches any character that is a digit.
     *
     * @return A parser that matches any character that is a digit.
     */
    public static Parser<Character> digit() {
        return new CharacterParser(Character::isDigit);
    }

    /**
     * Returns a parser that matches any character that is a letter.
     *
     * @return A parser that matches any character that is a letter.
     */
    public static Parser<Character> letter() {
        return new CharacterParser(Character::isLetter);
    }

    /**
     * Returns a parser that matches any character that is a letter or digit.
     *
     * @return A parser that matches any character that is a letter or digit.
     */
    public static Parser<Character> letterOrDigit() {
        return new CharacterParser(Character::isLetterOrDigit);
    }

    /**
     * Returns a parser that matches any character that is a lowercase letter.
     *
     * @return A parser that matches any character that is a lowercase letter.
     */
    public static Parser<Character> lowerCaseLetter() {
        return new CharacterParser(Character::isLowerCase);
    }

    /**
     * Returns a parser that matches any character that is a uppercase letter.
     *
     * @return A parser that matches any character that is a uppercase letter.
     */
    public static Parser<Character> upperCaseLetter() {
        return new CharacterParser(Character::isUpperCase);
    }

    /**
     * Returns a parser that matches any character that is white space.
     *
     * @return A parser that matches any character that is white space.
     */
    public static Parser<Character> whitespace() {
        return new CharacterParser(Character::isWhitespace);
    }

    public static Parser<Integer> signedInteger() {
        return new IntegerParser(true, 10, 1, Integer.MAX_VALUE);
    }

    public static Parser<Integer> unsignedInteger() {
        return new IntegerParser(false, 10, 1, Integer.MAX_VALUE);
    }

    public static <T, S> Parser<Either<T, S>> either(Parser<T> left, Parser<S> right) {
        return new ChoiceParser<>(left, right);
    }

    @SafeVarargs
    public static <T> Parser<T> any(Parser<T> head, Parser<T>... tail) {
        return any(head, List.of(tail));
    }

    private static <T> Parser<T> any(Parser<T> head, List<Parser<T>> tail) {
        int tailSize = tail.size();
        if (tailSize == 0) {
            return head;
        }

        if (tailSize == 1) {
            return new MapParser<>(
                new ChoiceParser<>(
                    head, tail.get(0)
                ),
                match -> match.getEither(l -> l, r -> r)
            );
        }

        return new MapParser<>(
            new ChoiceParser<>(
                head, any(tail.get(0), tail.subList(1, tailSize))
            ),
            match -> match.getEither(l -> l, r -> r)
        );
    }

    public static <T, S> Parser<Pair<T, S>> sequence(Parser<T> left, Parser<S> right) {
        return new SequenceParser<>(left, right);
    }

    public static <T, S, R> Parser<Triple<T, S, R>> sequence(Parser<T> left, Parser<S> middle, Parser<R> right) {
        return new MapParser<>(
            new SequenceParser<>(left,
                new SequenceParser<>(middle, right)
            ),
            match -> Triple.of(match.first(), match.second().first(), match.second().second())
        );
    }

    /**
     * Returns a parser that matches zero or one occurrence(s) of the given parser.
     *
     * @param <T> the type of the elements parsed by the given parser.
     * @param parser the parser to be repeated zero or one time(s).
     * @return a parser that matches zero or one occurrence(s) of the given parser.
     */
    public static <T> Parser<Optional<T>> zeroOrOne(Parser<T> parser) {
        return new OptionalParser<>(parser);
    }

    /**
     * Returns a parser that matches zero or more occurrences of the given parser.
     *
     * @param <T> the type of the elements parsed by the given parser.
     * @param parser the parser to be repeated zero or more times.
     * @return a parser that matches zero or more occurrences of the given parser.
     */
    public static <T> Parser<List<T>> zeroOrMore(Parser<T> parser) {
        return new RepeatParser<>(parser, 0, Integer.MAX_VALUE);
    }

    /**
     * Returns a parser that matches one or more occurrences of the specified parser.
     *
     * @param <T> the type of the elements parsed by the specified parser.
     * @param parser the parser to be repeated.
     * @return a parser that matches one or more occurrences of the specified parser.
     */
    public static <T> Parser<List<T>> oneOrMore(Parser<T> parser) {
        return new RepeatParser<>(parser, 1, Integer.MAX_VALUE);
    }

    /**
     * Returns a parser that parses the input using the specified parser a
     * number of times between the given lower and upper bounds.
     *
     * @param <T> the type of the elements being parsed
     * @param parser the parser to be repeated
     * @param lowerBound the minimum number of times the parser should be applied
     * @param upperBound the maximum number of times the parser should be applied
     * @return a parser that applies the given parser between lowerBound and upperBound times
     */
    public static <T> Parser<List<T>> repeat(Parser<T> parser, int lowerBound, int upperBound) {
        return new RepeatParser<>(parser, lowerBound, upperBound);
    }

    public static <T, S> Parser<T> map(Parser<S> parser, Supplier<? extends T> supplier) {
        return new MapParser<>(parser, supplier);
    }

    public static <T, S> Parser<T> map(Parser<S> parser, Function<? super S, ? extends T> function) {
        return new MapParser<>(parser, function);
    }
}
