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

import com.ragnvaldr.alphaomega.scanning.Scanner;
import com.ragnvaldr.alphaomega.util.Unused;

/**
 * The {@link OmitParser} class is a parser that can be used to omit the value
 * returned by another parser on a successfull match.
 */
public final class OmitParser<T> implements Parser<Unused> {

    private Parser<T> parser;

    /**
     * Creates a new {@link OmitParser} that omits the value of the specified
     * parser when that parser returns a successfull match.
     *
     * @param parser The parser to omit the parse result of.
     */
    OmitParser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<Unused> parse(Scanner scanner) {
        var parseResult = parser.parse(scanner);
        if (parseResult.isFailure()) {
            return ParseResult.failure();
        } else {
            return ParseResult.success(null);
        }
    }
}
