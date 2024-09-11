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

public final class PatternFactory {
    
    public static Pattern literal(String literal) {
        return new StringPattern(literal);
    }

    public static Pattern literal(Character literal) {
        return new CharacterPattern((character) -> character == literal);
    }

    public static Pattern digit() {
        return new CharacterPattern(Character::isDigit);
    }

    public static Pattern letter() {
        return new CharacterPattern(Character::isLetter);
    }

    public static Pattern letterOrDigit() {
        return new CharacterPattern(Character::isLetterOrDigit);
    }

    public static Pattern lowerCaseLetter() {
        return new CharacterPattern(Character::isLowerCase);
    }

    public static Pattern upperCaseLetter() {
        return new CharacterPattern(Character::isUpperCase);
    }

    public static Pattern whitespace() {
        return new CharacterPattern(Character::isWhitespace);
    }

    public static Pattern range(char firstCharacter, char lastCharacter) {
        return new CharacterPattern((character) -> character >= firstCharacter && character <= lastCharacter);
    }

    public static Pattern alternative(Pattern... patterns) {
        return new AlternativePattern(patterns);
    }

    public static Pattern sequence(Pattern... patterns) {
        return new SequencePattern(patterns);
    }

    public static Pattern optional(Pattern pattern) {
        return new RepeatPattern(pattern, 0, 1);
    }
    
    public static Pattern kleenePlus(Pattern pattern) {
        return new RepeatPattern(pattern, 1, Integer.MAX_VALUE);
    }

    public static Pattern kleeneStar(Pattern pattern) {
        return new RepeatPattern(pattern, 0, Integer.MAX_VALUE);
    }

    public static Pattern repeat(Pattern pattern, int count) {
        return new RepeatPattern(pattern, count, count);
    }

    public static Pattern repeat(Pattern pattern, int lowerBound, int upperBound) {
        return new RepeatPattern(pattern, lowerBound, upperBound);
    }
}