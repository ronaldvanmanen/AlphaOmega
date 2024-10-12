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

    public OptionalParser(Parser<T> parser) {
        this.parser = parser;
    }

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
