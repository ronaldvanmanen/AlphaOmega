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

import java.util.function.Predicate;

import com.ragnvaldr.alphaomega.scanning.Scanner;

/**
 * The {@link CharacterParser} class is a parser that matches single characters.
 */
public final class CharacterParser implements Parser<Character> {

    private Predicate<Character> predicate;

    /**
     * Creates a new {@link CharacterParser} that uses the specified predicate
     * to match single characters.
     *
     * @param predicate A non-interfering, stateless predicate to apply to a
     * character to determine if that character returns a successfull match or
     * not.
     */
    public CharacterParser(Predicate<Character> predicate) {
        this.predicate = predicate;
    }

    /**
     * Parses a single character from the provided scanner.
     *
     * @param scanner The {@link Scanner} providing the input to be parsed.
     *
     * @return A {@link ParseResult.Success} containing the parsed character if
     *         successful, or a {@link ParseResult.Failure} if the character
     *         does not match the predicate.
     */
    public ParseResult<Character> parse(Scanner scanner) {
        var position = scanner.getPosition();

        var value = scanner.read();
        if (value != -1) {
            var character = (char)value;
            if (predicate.test(character)) {
                return ParseResult.success(character);
            }
        }

        scanner.setPosition(position);

        return ParseResult.failure();
    }

    CharacterParser negate() {
        return new CharacterParser(predicate.negate());
    }
}
