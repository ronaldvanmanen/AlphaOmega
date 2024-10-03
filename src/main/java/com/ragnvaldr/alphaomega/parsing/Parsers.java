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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ragnvaldr.alphaomega.util.Either;
import com.ragnvaldr.alphaomega.util.Pair;
import com.ragnvaldr.alphaomega.util.Triple;
import com.ragnvaldr.alphaomega.util.Unused;

public final class Parsers {

    private Parsers() {}

    public static CharacterParser literal(char character) {
        return new CharacterParser((c) -> c == character);
    }

    public static CharacterParser range(char firstCharacter, char lastCharacter) {
        return new CharacterParser((character) -> character >= firstCharacter && character <= lastCharacter);
    }

    public static CharacterParser anyOf(Character... characters) {
        return anyOf(Set.of(characters));
    }

    private static CharacterParser anyOf(Set<Character> characters) {
        return new CharacterParser((c) -> characters.contains(c));
    }

    public static CharacterParser noneOf(Character... characters) {
        return noneOf(Set.of(characters));
    }

    private static CharacterParser noneOf(Set<Character> characters) {
        return new CharacterParser((c) -> !characters.contains(c));
    }

    public static CharacterParser digit() {
        return new CharacterParser(Character::isDigit);
    }

    public static CharacterParser letter() {
        return new CharacterParser(Character::isLetter);
    }

    public static CharacterParser letterOrDigit() {
        return new CharacterParser(Character::isLetterOrDigit);
    }

    public static CharacterParser lowerCaseLetter() {
        return new CharacterParser(Character::isLowerCase);
    }

    public static CharacterParser upperCaseLetter() {
        return new CharacterParser(Character::isUpperCase);
    }

    public static CharacterParser whitespace() {
        return new CharacterParser(Character::isWhitespace);
    }

    public static CharacterParser not(CharacterParser parser) {
        return parser.negate();
    }

    public static StringParser literal(String literal) {
        return new StringParser(literal);
    }

    public static IntegerParser signedInteger() {
        return new IntegerParser(true, 10, 1, Integer.MAX_VALUE);
    }

    public static IntegerParser unsignedInteger() {
        return new IntegerParser(false, 10, 1, Integer.MAX_VALUE);
    }

    public static <T, S> AlternativeParser<T, S> either(Parser<T> left, Parser<S> right) {
        return new AlternativeParser<>(left, right);
    }

    @SafeVarargs
    public static <T> Parser<T> anyOf(Parser<T> head, Parser<T>... tail) {
        return anyOf(head, List.of(tail));
    }

    private static <T> Parser<T> anyOf(Parser<T> head, List<Parser<T>> tail) {
        int tailSize = tail.size();
        if (tailSize == 0) {
            return head;
        }

        if (tailSize == 1) {
            return new TransformParser<>(
                new AlternativeParser<>(
                    head, tail.get(0)
                ),
                Either::getLeftOrRight
            );
        }

        return new TransformParser<>(
            new AlternativeParser<>(
                head, anyOf(tail.get(0), tail.subList(1, tailSize))
            ),
            Either::getLeftOrRight
        );
    }

    public static <T, S> SequenceParser<T, S> sequence(Parser<T> left, Parser<S> right) {
        return new SequenceParser<>(left, right);
    }

    public static <T, S> Parser<T> sequence(Parser<T> left, OmitParser<S> right) {
        return new TransformParser<>(new SequenceParser<T, Unused>(left, right), Pair::first);
    }

    public static <T, S> Parser<S> sequence(OmitParser<T> left, Parser<S> right) {
        return new TransformParser<>(new SequenceParser<Unused, S>(left, right), Pair::second);
    }

    public static <T, S, R> Parser<Triple<T, S, R>> sequence(Parser<T> left, Parser<S> middle, Parser<R> right) {
        return new TransformParser<>(
            new SequenceParser<>(left,
                new SequenceParser<>(middle, right)
            ),
            match -> Triple.of(match.first(), match.second())
        );
    }

    public static <T, S, R> Parser<Pair<T, R>> sequence(Parser<T> left, OmitParser<S> middle, Parser<R> right) {
        return new TransformParser<>(
            new SequenceParser<>(left,
                new SequenceParser<>(middle, right)
            ),
            match -> Pair.of(match.first(), match.second().second())
        );
    }

    public static <T, S, R> Parser<S> sequence(OmitParser<T> left, Parser<S> middle, OmitParser<R> right) {
        return new TransformParser<>(
            new SequenceParser<>(left,
                new SequenceParser<>(middle, right)
            ),
            match -> match.second().first()
        );
    }

    public static <T> OptionalParser<T> optional(Parser<T> parser) {
        return new OptionalParser<>(parser);
    }

    public static <T> RepeatParser<T> zeroOrMore(Parser<T> parser) {
        return new RepeatParser<>(parser, 0, Integer.MAX_VALUE);
    }

    public static <T> RepeatParser<T> oneOrMore(Parser<T> parser) {
        return new RepeatParser<>(parser, 1, Integer.MAX_VALUE);
    }

    public static <T> RepeatParser<T> repeat(Parser<T> parser, int lowerBound, int upperBound) {
        return new RepeatParser<>(parser, lowerBound, upperBound);
    }

    public static <T, S> TransformParser<T, S> transform(Parser<S> parser, Supplier<? extends T> transform) {
        return new TransformParser<>(parser, transform);
    }

    public static <T, S> TransformParser<T, S> transform(Parser<S> parser, Function<? super S, ? extends T> transform) {
        return new TransformParser<>(parser, transform);
    }

    public static <T> OmitParser<T> omit(Parser<T> parser) {
        return new OmitParser<>(parser);
    }
}
