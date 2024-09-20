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

package com.ragnvaldr.alphaomega.regex;

import java.util.List;

final class Patterns {

    private Patterns() {}

    public static CharacterPattern character(Character literal) {
        return new CharacterPattern((character) -> character == literal);
    }

    public static CharacterPattern digit() {
        return new CharacterPattern(Character::isDigit);
    }

    public static CharacterPattern letter() {
        return new CharacterPattern(Character::isLetter);
    }

    public static CharacterPattern letterOrDigit() {
        return new CharacterPattern(Character::isLetterOrDigit);
    }

    public static CharacterPattern lowerCaseLetter() {
        return new CharacterPattern(Character::isLowerCase);
    }

    public static CharacterPattern upperCaseLetter() {
        return new CharacterPattern(Character::isUpperCase);
    }

    public static CharacterPattern whitespace() {
        return new CharacterPattern(Character::isWhitespace);
    }

    public static CharacterPattern range(char firstCharacter, char lastCharacter) {
        return new CharacterPattern((character) -> character >= firstCharacter && character <= lastCharacter);
    }

    public static CharacterPattern any() {
        return new CharacterPattern((character) -> character != '\n');
    }

    public static Pattern string(String literal) {
        return new StringPattern(literal);
    }

    public static Pattern emptyString() {
        return new StringPattern("");
    }

    public static Pattern oneOf(Pattern... patterns) {
        return new AlternativePattern(patterns);
    }

    public static Pattern oneOf(List<Pattern> patterns) {
        return new AlternativePattern(patterns);
    }

    public static Pattern sequence(Pattern... patterns) {
        return new SequencePattern(patterns);
    }

    public static Pattern sequence(List<Pattern> patterns) {
        return new SequencePattern(patterns);
    }

    public static Pattern zeroOrOne(Pattern pattern) {
        return new RepeatPattern(pattern, 0, 1);
    }
    
    public static Pattern zeroOrMore(Pattern pattern) {
        return new RepeatPattern(pattern, 0, Integer.MAX_VALUE);
    }

    public static Pattern oneOrMore(Pattern pattern) {
        return new RepeatPattern(pattern, 1, Integer.MAX_VALUE);
    }

    public static Pattern repeat(Pattern pattern, int count) {
        return new RepeatPattern(pattern, count, count);
    }

    public static Pattern repeat(Pattern pattern, int lowerBound, int upperBound) {
        return new RepeatPattern(pattern, lowerBound, upperBound);
    }

}