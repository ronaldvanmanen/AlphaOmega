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

package com.ragnvaldr.alphaomega.parsers;

import java.util.List;
import com.ragnvaldr.alphaomega.util.Either;
import com.ragnvaldr.alphaomega.util.Pair;

public final class ParserFactory {
    
    public static Parser<String> literal(String literal) {
        return new StringParser(literal);
    }

    public static Parser<Character> literal(Character literal) {
        return new CharacterParser((character) -> character == literal);
    }

    public static Parser<Character> digit() {
        return new CharacterParser(Character::isDigit);
    }

    public static Parser<Character> letter() {
        return new CharacterParser(Character::isLetter);
    }

    public static Parser<Character> letterOrDigit() {
        return new CharacterParser(Character::isLetterOrDigit);
    }

    public static Parser<Character> lowerCaseLetter() {
        return new CharacterParser(Character::isLowerCase);
    }

    public static Parser<Character> upperCaseLetter() {
        return new CharacterParser(Character::isUpperCase);
    }

    public static Parser<Character> whitespace() {
        return new CharacterParser(Character::isWhitespace);
    }

    public static Parser<Character> range(char firstCharacter, char lastCharacter) {
        return new CharacterParser((character) -> character >= firstCharacter && character <= lastCharacter);
    }

    public static <T, S> Parser<Either<T, S>> alternative(Parser<T> left, Parser<S> right) {
        return new AlternativeParser<T, S>(left, right);
    }

    public static <T, S> Parser<Pair<T, S>> sequence(Parser<T> left, Parser<S> right) {
        return new SequenceParser<T, S>(left, right);
    }

    public static <T> Parser<List<T>> optional(Parser<T> parser) {
        return new RepeatParser<T>(parser, 0, 1);
    }
    
    public static <T> Parser<List<T>> kleenePlus(Parser<T> parser) {
        return new KleenePlusParser<T>(parser);
    }

    public static <T> Parser<List<T>> kleeneStar(Parser<T> parser) {
        return new KleeneStarParser<T>(parser);
    }

    public static <T> Parser<List<T>> repeat(Parser<T> parser, int count) {
        return new RepeatParser<T>(parser, count);
    }

    public static <T> Parser<List<T>> repeat(Parser<T> parser, int lowerBound, int upperBound) {
        return new RepeatParser<T>(parser, lowerBound, upperBound);
    }
}