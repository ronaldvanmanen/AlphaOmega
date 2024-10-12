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

import java.util.Optional;

import com.ragnvaldr.alphaomega.scanning.Scanner;

/**
 * The {@link OptionalParser} class is a parser that returns the parse result
 * of another parser when that parser is successfully matched. Otherwise, the
 * {@link OptionalParser} will return an empty parse result.
 */
public final class OptionalParser<T> implements Parser<Optional<T>> {

    private Parser<T> parser;

    /**
     * Creates a new {@link OptionalParser}.
     *
     * @param parser The parser to be matched zero or one time(s).
     */
    public OptionalParser(Parser<T> parser) {
        this.parser = parser;
    }

    /**
     * Parses the input from the given scanner and returns a ParseResult containing an Optional value.
     * If the parsing is successful, the result will contain the parsed value wrapped in an Optional.
     * If the parsing fails, the scanner's position is reset to its original state and the result will contain an empty Optional.
     *
     * @param scanner The {@link Scanner} providing the input to be parsed.
     *
     * @return a ParseResult containing an Optional value, which is either the parsed value or empty if parsing fails
     */
    @Override
    public ParseResult<Optional<T>> parse(Scanner scanner) {
        var position = scanner.getPosition();
        var parseResult = parser.parse(scanner);
        if (parseResult.isSuccess()) {
            return ParseResult.success(
                Optional.of(
                    parseResult.getValue()
                )
            );
        }

        scanner.setPosition(position);
        return ParseResult.success(
            Optional.empty()
        );
    }
}
