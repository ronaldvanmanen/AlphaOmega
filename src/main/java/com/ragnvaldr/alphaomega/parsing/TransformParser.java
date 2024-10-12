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

import java.util.function.Function;
import java.util.function.Supplier;

import com.ragnvaldr.alphaomega.scanning.Scanner;

/**
 * The {@link TransformParser} class is a parser that transforms the value
 * returned by another parser on a successfull match.
 */
public final class TransformParser<TTarget, TSource> implements Parser<TTarget> {

    private Parser<TSource> parser;

    private Function<? super TSource, ? extends TTarget> transform;

    /**
     * Creates a new {@link TransformParser} with the specified parser.
     *
     * @param parser The parser to transform the parse result of.
     * @param transform The method used to transform the value of the parse
     *                  returned by {@code parser} on successfull match.
     */
    public TransformParser(Parser<TSource> parser, Supplier<? extends TTarget> transform) {
        this(parser, _ -> transform.get());
    }

    /**
     * Creates a new {@link TransformParser}.
     *
     * @param parser The parser to transform the parse result of.
     * @param transform The method used to transform the value of the parse
     * result returned by {@code parser} on successfull match.
     */
    public TransformParser(Parser<TSource> parser, Function<? super TSource, ? extends TTarget> transform) {
        this.parser = parser;
        this.transform = transform;
    }

    public ParseResult<TTarget> parse(Scanner scanner) {
        var parseResult = parser.parse(scanner);
        if (parseResult.isSuccess()) {
            return ParseResult.success(
                transform.apply(parseResult.getValue())
            );
        }
        return ParseResult.failure();
    }
}
