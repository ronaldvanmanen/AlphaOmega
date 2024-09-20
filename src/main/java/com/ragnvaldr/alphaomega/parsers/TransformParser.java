package com.ragnvaldr.alphaomega.parsers;

import java.util.function.Function;
import java.util.function.Supplier;

import com.ragnvaldr.alphaomega.Scanner;

final class TransformParser<TTarget, TSource> implements Parser<TTarget> {
 
    private Parser<TSource> parser;

    private Function<? super TSource, ? extends TTarget> transform;

    public TransformParser(Parser<TSource> parser, Supplier<? extends TTarget> transform) {
        this(parser, _ -> transform.get());
    }

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