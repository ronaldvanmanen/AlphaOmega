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

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import com.ragnvaldr.alphaomega.*;
import com.ragnvaldr.alphaomega.parsers.*;
import com.ragnvaldr.alphaomega.util.*;

public final class PatternParser implements Parser<Pattern> {

    private final Rule<Pattern> regex = new Rule<>();

    private final Rule<Pattern> alternation = new Rule<>();

    private final Rule<Pattern> expr = new Rule<>();

    private final Rule<Pattern> element = new Rule<>();

    private final Rule<Quantifier> quantifier = new Rule<>();

    private final Rule<Quantifier> quantity = new Rule<>();

    private final Rule<Quantifier> quantRange = new Rule<>();

    private final Rule<Quantifier> quantMin = new Rule<>();

    private final Rule<Quantifier> quantExact = new Rule<>();

    private final Rule<Pattern> atom = new Rule<>();
    
    private final Rule<Pattern> normalChar = new Rule<>();

    private final Rule<Pattern> charClass = new Rule<>();

    private final Rule<Pattern> charClassExpr = new Rule<>();

    private final Rule<Pattern> charGroup = new Rule<>();

    private final Rule<Pattern> posCharGroup = new Rule<>();

    private final Rule<Pattern> negCharGroup = new Rule<>();

    private final Rule<Pattern> charClassSub = new Rule<>();

    private final Rule<Pattern> charRange = new Rule<>();

    private final Rule<Pattern> charClassEsc = new Rule<>();

    private final Rule<Pattern> wildcardEsc = new Rule<>();

    public PatternParser() {

        regex.assign(
            new TransformParser<>(
                new OptionalParser<>(
                    alternation
                ),
                match -> match.orElseGet(
                    StringPattern::emptyString
                )
            )
        );

        alternation.assign(
            new TransformParser<>(
                new SequenceParser<>(
                    expr,
                    new KleeneStarParser<>(
                        new TransformParser<>(
                            new SequenceParser<>(
                                new CharacterParser('|'),
                                new OptionalParser<>(expr)
                            ),
                            Pair::second
                        )
                    )
                ),
                match -> {
                    var head = match.first();
                    var tail = match.second()
                        .stream()
                        .map(optionalPattern ->
                            optionalPattern.orElseGet(
                                StringPattern::emptyString
                            )
                        )
                        .collect(Collectors.toList());

                    if (tail.isEmpty()) {
                        return head;
                    }

                    tail.addFirst(head);

                    return new AlternativePattern(tail);
                }
            )
        );

        expr.assign(
            new TransformParser<>(
                new KleenePlusParser<>(
                    element
                ),
                patterns -> {
                    return new SequencePattern(patterns);
                }
            )
        );

        element.assign(
            new TransformParser<>(
                new SequenceParser<>(
                    atom,
                    new OptionalParser<>(quantifier)
                ),
                match -> {
                    var atom = match.first();

                    var optionalQuantifier = match.second();
                    if (!optionalQuantifier.isPresent()) {
                        return atom;
                    }

                    var quantifier = optionalQuantifier.get();
                    switch (quantifier.getType()) {
                        case KLEENE_PLUS:
                            return new RepeatPattern(atom, 1, Integer.MAX_VALUE);
                        case KLEENE_STAR:
                            return new RepeatPattern(atom, 0, Integer.MAX_VALUE);
                        case OPTIONAL:
                            return new RepeatPattern(atom, 0, 1);
                        case RANGE:
                            var range = quantifier.getRange();
                            var minimum = range.getMinimum();
                            var maximum = range.getMaximum();
                            return new RepeatPattern(atom, minimum, maximum);
                        default:
                            throw new PatternSyntaxException("Invalid range");
                    }
                }
            )
        );

        // [4] quantifier ::= [?*+] | ( '{' quantity '}' )
        quantifier.assign(
            new TransformParser<>(
                new AlternativeParser<>(
                    new TransformParser<>(
                        new CharacterParser(c -> c == '?' || c == '*' || c == '+'),
                        match -> switch (match) {
                            case '?' -> Quantifier.optional();
                            case '*' -> Quantifier.kleeneStar();
                            case '+' -> Quantifier.kleenePlus();
                            default -> throw new PatternSyntaxException("Invalid quantifier character");
                        }
                    ),
                    new TransformParser<>(
                        new SequenceParser<>(
                            new CharacterParser('{'),
                            new TransformParser<>(
                                new SequenceParser<>(
                                    quantity, 
                                    new CharacterParser('}')
                                ),
                                Pair::first
                            )
                        ),
                        Pair::second
                    )
                ),
                Either::getLeftOrRight
            )
        );

        // [5] quantity ::= quantRange | quantMin | quantExact
        quantity.assign(
            new TransformParser<>(
                new AlternativeParser<>(
                    quantRange,
                    new TransformParser<>(
                        new AlternativeParser<>(
                            quantMin, quantExact
                        ),
                        Either::getLeftOrRight
                    )
                ),
                Either::getLeftOrRight
            )
        );

        // [6] quantRange ::= [0-9]+ ',' [0-9]+
        quantRange.assign(
            new TransformParser<>(
                new SequenceParser<>(
                    IntegerParser.unsigned(),
                    new TransformParser<>(
                        new SequenceParser<>(
                            new CharacterParser(','),
                            IntegerParser.unsigned()
                        ),
                        Pair::second
                    )
                ),
                Quantifier::range
            )
        );

        // [7] quantMin ::= [0-9]+ ','
        quantMin.assign(
            new TransformParser<>(
                new TransformParser<>(
                    new SequenceParser<>(
                        IntegerParser.unsigned(),
                        new CharacterParser(',')
                    ),
                    Pair::first
                ),
                Quantifier::min
            )
        );

        // [8] quantExact ::= [0-9]+
        quantExact.assign(
            new TransformParser<>(
                IntegerParser.unsigned(),
                Quantifier::exact
            )
        );

        // [9] atom ::= Char | charClass | ( '(' regExp ')' )
        atom.assign(
            new TransformParser<>(
                new AlternativeParser<>(
                    normalChar, 
                    new TransformParser<>(
                        new AlternativeParser<>(
                            charClass,
                            new TransformParser<>(
                                new SequenceParser<>(
                                    new CharacterParser('('),
                                    new TransformParser<>(
                                        new SequenceParser<>(
                                            regex,
                                            new CharacterParser(')')
                                        ),
                                        Pair::first
                                    )
                                ),
                                Pair::second
                            )
                        ),
                        Either::getLeftOrRight
                    )
                ),
                Either::getLeftOrRight
            )
        );

        // [10] char ::= [^.\?*+()|#x5B#x5D]
        normalChar.assign(
            new TransformParser<>(
                new CharacterParser(c ->
                    c != '.' &&
                    c != '\\' &&
                    c != '?'  &&
                    c != '*' &&
                    c != '+' &&
                    c != '(' &&
                    c != ')' &&
                    c != '|' &&
                    c != '[' &&
                    c != ']'
                ),
                match -> new CharacterPattern(c -> c == match)
            )
        );

        // [11] charClass ::= charClassEsc | charClassExpr | WildcardEsc
        charClass.assign(
            new TransformParser<>(
                new AlternativeParser<>(
                    charClassEsc,
                    new TransformParser<>(
                        new AlternativeParser<>(
                            charClassExpr, wildcardEsc
                        ),
                        Either::getLeftOrRight
                    )
                ),
                Either::getLeftOrRight
            )
        );

        // [12] charClassExpr ::= '[' charGroup ']'
        charClassExpr.assign(
            new TransformParser<>(
                new SequenceParser<>(
                    new CharacterParser('['),
                    new TransformParser<>(
                        new SequenceParser<>(
                            charGroup, new CharacterParser(']')
                        ),
                        Pair::first
                    )
                ),
                Pair::second
            )
        );

        // [13] charGroup ::= posCharGroup | negCharGroup | charClassSub
        charGroup.assign(
            new TransformParser<>(
                new AlternativeParser<>(
                    posCharGroup,
                    new TransformParser<>(
                        new AlternativeParser<>(
                            negCharGroup, charClassSub
                        ),
                        Either::getLeftOrRight
                    )
                ),
                Either::getLeftOrRight
            )
        );

        // [14] posCharGroup ::= ( charRange | charClassEsc )+
        posCharGroup.assign(
            new TransformParser<>(
                new KleenePlusParser<>(
                    new TransformParser<>(
                        new AlternativeParser<>(
                            charRange, charClassEsc
                        ),
                        Either::getLeftOrRight
                    )
                ),
                match -> {
                    if (match.size() == 1) {
                        return match.get(0);
                    } else {
                        return new SequencePattern(match);
                    }
                }
            )
        );

        // [15] negCharGroup ::= '^' posCharGroup
        negCharGroup.assign(
            new TransformParser<>(
                new SequenceParser<>(
                    new CharacterParser('^'), posCharGroup
                ),
                Pair::second
            )
        );


    }

    @Override
    public ParseResult<Pattern> parse(Scanner scanner) {
        var parseResult = regex.parse(scanner);
        if (parseResult.isSuccess()){
            return parseResult;
        }
        return ParseResult.failure();
    }
}

final class Range {
    private final int minimum;

    private final int maximum;

    public Range(int count) {
        this(count, count);
    }

    public Range(int minimum, int maximum) {
        if (minimum < 0) {
            throw new IllegalArgumentException("Minimum must be greater than or equal to zero");
        }

        if (maximum < minimum) {
            throw new IllegalArgumentException("Maximum must be greater than or equal to minimum.");
        }

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }
}

enum QuantifierType {
    OPTIONAL,
    KLEENE_STAR,
    KLEENE_PLUS,
    RANGE
}

final class Quantifier {
    private QuantifierType type;

    @Nullable
    private final Range range;

    private Quantifier(QuantifierType type) {
        this(type, null);
    }

    private Quantifier(Range range) {
        this(QuantifierType.RANGE, range);
    }

    private Quantifier(QuantifierType type, @Nullable Range range) {
        if (type == QuantifierType.RANGE && range == null) {
            throw new IllegalArgumentException("Range is missing");
        }
        this.type = type;
        this.range = range;
    }
    
    public QuantifierType getType() {
        return type;
    }

    @SuppressWarnings("null")
    public Range getRange() {
        if (range == null) {
            throw new IllegalStateException("Quantifier is not a range");
        }
        return range;
    }

    public static Quantifier optional() {
        return new Quantifier(QuantifierType.OPTIONAL);
    }

    public static Quantifier kleeneStar() {
        return new Quantifier(QuantifierType.KLEENE_STAR);
    }

    public static Quantifier kleenePlus() {
        return new Quantifier(QuantifierType.KLEENE_PLUS);
    }

    public static Quantifier range(int minimum, int maximum) {
        return new Quantifier(QuantifierType.RANGE,
            new Range(minimum, maximum)
        );
    }

    public static Quantifier range(Pair<Integer, Integer> range) {
        return new Quantifier(QuantifierType.RANGE,
            new Range(range.first(), range.second())
        );
    }

    public static Quantifier exact(int count) {
        return new Quantifier(QuantifierType.RANGE, new Range(count));
    }

    public static Quantifier min(int minimum) {
        return new Quantifier(QuantifierType.RANGE,
            new Range(minimum, Integer.MAX_VALUE)
        );
    }
}
