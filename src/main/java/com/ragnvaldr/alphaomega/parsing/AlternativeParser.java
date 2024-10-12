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
import com.ragnvaldr.alphaomega.util.Either;

/**
 * The {@link AlternativeParser} class is a parser that returns the parse result
 * of either one of two parsers.
 *
 * The left parser is tried first and if, and only if, it succeeds it's result
 * will be returned immediately. Otherwise, the right operand will be tried and
 * if, and only if, successfully matched it's result will be returned.
 */
public final class AlternativeParser<T, S> implements Parser<Either<T, S>> {

    private Parser<T> first;

    private Parser<S> second;

    /**
     * Creates a {@link AlternativeParser} with the specified operands.
     *
     * @param first The first alternative.
     * @param second The second alternative.
     */
    public AlternativeParser(Parser<T> first, Parser<S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public ParseResult<Either<T, S>> parse(Scanner scanner) {
        var position = scanner.getPosition();

        var firstParseResult = first.parse(scanner);
        if (firstParseResult.isSuccess()) {
            return ParseResult.success(
                Either.left(firstParseResult.getValue())
            );
        }

        scanner.setPosition(position);

        var secondParseResult = second.parse(scanner);
        if (secondParseResult.isSuccess()) {
            return ParseResult.success(
                Either.right(secondParseResult.getValue())
            );
        }

        scanner.setPosition(position);

        return ParseResult.failure();
    }
}
