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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ragnvaldr.alphaomega.scanning.Scanner;

/**
 * The {@link RepeatParser} class is a parser that attempts to parse a sequence
 * of elements using a specified parser, within a given range of repetitions.
 *
 * @param <T> The type of elements to be parsed.
 */
public final class RepeatParser<T> implements Parser<List<T>> {

    private Parser<T> parser;
    private int lowerBound;
    private int upperBound;

    /**
     * Creates a {@link RepeatParser} with the specified parser and bounds.
     *
     * @param parser The parser to be used for parsing elements.
     * @param lowerBound The minimum number of times the parser should succeed.
     * @param upperBound The maximum number of times the parser should succeed.
     * @throws IllegalArgumentException if lowerBound is negative or upperBound is less than lowerBound.
     */
    public RepeatParser(Parser<T> parser, int lowerBound, int upperBound) {
        if (lowerBound < 0 || upperBound < lowerBound) {
            throw new IllegalArgumentException("Invalid bounds");
        }

        this.parser = parser;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Parses the input from the given scanner, attempting to match the parser
     * between the specified lower and upper bounds.
     *
     * @param scanner The {@link Scanner} providing the input to be parsed.
     *
     * @return A ParseResult containing a list of parsed elements if the parsing
     *         succeeds within the specified bounds, or a failure result otherwise.
     */
    @Override
    public ParseResult<List<T>> parse(Scanner scanner) {

        var position = scanner.getPosition();
        var items = new ArrayList<T>();
        var count = 0;

        while (count < upperBound) {
            var parseResult = parser.parse(scanner);
            if (parseResult.isFailure()) {
                break;
            }
            items.add(parseResult.getValue());
            ++count;
        }

        if (count >= lowerBound && count <= upperBound) {
            return ParseResult.success(
                Collections.unmodifiableList(items)
            );
        }

        scanner.setPosition(position);

        return ParseResult.failure();
    }

}
