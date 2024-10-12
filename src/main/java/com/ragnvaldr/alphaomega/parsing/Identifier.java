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

/**
 * The {@link Identifier} class is a non-terminal parser that can be used to identify and reference a parser expression.
 */
public final class Identifier<T> implements Parser<T> {

    private Parser<? extends T> _parser = new FailureParser<>();

    /**
     * Parses the input from the given scanner using the internal parser.
     *
     * @param scanner The {@link Scanner} providing the input to be parsed.
     *
     * @return a ParseResult containing the parsed value if successful, or a failure result if parsing fails
     */
    public ParseResult<T> parse(Scanner scanner) {
        var parseResult = _parser.parse(scanner);
        if (parseResult.isSuccess()) {
            return ParseResult.success((T)parseResult.getValue());
        }
        return ParseResult.failure();
    }

    /**
     * Assigns the specified parser expression to this identifier.
     *
     * @param parser The parser expression to assign to this identifier.
     */
    public void is(Parser<? extends T> parser) {
        _parser = parser;
    }
}
