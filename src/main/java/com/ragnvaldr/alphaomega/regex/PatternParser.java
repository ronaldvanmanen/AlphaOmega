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


import com.ragnvaldr.alphaomega.*;
import com.ragnvaldr.alphaomega.parsers.*;
import com.ragnvaldr.alphaomega.util.*;

import static com.ragnvaldr.alphaomega.parsers.Parsers.*;

final class PatternParser implements Parser<Pattern> {

    private final Identifier<Pattern> regex = new Identifier<>();

    private final Identifier<Pattern> branch = new Identifier<>();

    private final Identifier<Pattern> piece = new Identifier<>();

    private final Identifier<Quantifier> quantifier = new Identifier<>();

    private final Identifier<Range> quantity = new Identifier<>();

    private final Identifier<Range> quantityRange = new Identifier<>();

    private final Identifier<Range> quantityMin = new Identifier<>();

    private final Identifier<Range> quantityExact = new Identifier<>();

    private final Identifier<Pattern> atom = new Identifier<>();

    private final Identifier<Pattern> normalCharacter = new Identifier<>();

    private final Identifier<Pattern> characterClass = new Identifier<>();

    private final Identifier<CharacterPattern> characterClassBracketed = new Identifier<>();

    private final Identifier<CharacterPattern> characterGroup = new Identifier<>();

    private final Identifier<CharacterPattern> characterRange = new Identifier<>();

    private final Identifier<CharacterPattern> multiCharacterRange = new Identifier<>();

    private final Identifier<CharacterPattern> singleCharacterRange = new Identifier<>();

    private final Identifier<CharacterPattern> characterClassEscape = new Identifier<>();

    private final Identifier<CharacterPattern> singleCharacterEscape = new Identifier<>();

    private final Identifier<CharacterPattern> multiCharacterEscape = new Identifier<>();

    private final Identifier<CharacterPattern> wildcardEscape = new Identifier<>();

    private final Identifier<Character> pipe = new Identifier<>();

    private final Identifier<Character> question = new Identifier<>();

    private final Identifier<Character> star = new Identifier<>();

    private final Identifier<Character> plus = new Identifier<>();

    private final Identifier<Character> dot = new Identifier<>();

    private final Identifier<Character> comma = new Identifier<>();

    private final Identifier<Character> leftBrace = new Identifier<>();

    private final Identifier<Character> rightBrace = new Identifier<>();

    private final Identifier<Character> leftParen = new Identifier<>();

    private final Identifier<Character> rightParen = new Identifier<>();

    private final Identifier<Character> leftBracket = new Identifier<>();

    private final Identifier<Character> rightBracket = new Identifier<>();

    private final Identifier<Character> circumflex = new Identifier<>();

    private final Identifier<Integer> integer = new Identifier<>();

    public PatternParser() {

        regex.is(
            transform(
                sequence(branch,
                    zeroOrMore(
                        sequence(
                            omit(pipe), optional(branch)
                        )
                    )
                ),
                match -> {
                    var firstBranch = match.first();
                    var optionalBranches = match.second();
                    var pattern = optionalBranches.stream()
                        .map(p -> p.orElseGet(Patterns::emptyString))
                        .reduce(firstBranch, (a, b) -> Patterns.anyOf(a, b));

                    return pattern;
                }
            )
        );

        branch.is(
            transform(
                oneOrMore(piece), patterns -> Patterns.sequence(patterns)
            )
        );

        piece.is(
            transform(
                sequence(
                    atom, optional(quantifier)
                ),
                match -> {
                    var atom = match.first();

                    var optionalQuantifier = match.second();
                    if (!optionalQuantifier.isPresent()) {
                        return atom;
                    }

                    var quantifier = optionalQuantifier.get();
                    switch (quantifier.getType()) {
                        case OPTIONAL: return Patterns.zeroOrOne(atom);
                        case ZERO_OR_MORE: return Patterns.zeroOrMore(atom);
                        case ONE_OR_MORE: return Patterns.oneOrMore(atom);
                        case RANGE:
                            var range = quantifier.getRange();
                            var minimum = range.getMinimum();
                            var maximum = range.getMaximum();
                            return Patterns.repeat(atom, minimum, maximum);
                        default:
                            throw new PatternSyntaxException("Invalid range");
                    }
                }
            )
        );

        quantifier.is(
            anyOf(
                transform(question, Quantifier::optional),
                transform(star, Quantifier::zeroOrMore),
                transform(plus, Quantifier::oneOrMore),
                transform(
                    sequence(
                        omit(leftBrace), quantity, omit(rightBrace)
                    ),
                    Quantifier::range
                )
            )
        );

        quantity.is(
            anyOf(quantityRange, quantityMin, quantityExact)
        );

        quantityRange.is(
            transform(
                sequence(integer, omit(comma), integer), Range::of
            )
        );

        quantityMin.is(
            transform(
                sequence(integer, omit(comma)), Range::atLeast
            )
        );

        quantityExact.is(
            transform(
                integer, Range::singleton
            )
        );

        atom.is(
            anyOf(
                normalCharacter,
                characterClass,
                sequence(
                    omit(leftParen), regex, omit(rightParen)
                )
            )
        );

        normalCharacter.is(
            transform(
                noneOf('.', '\\', '?', '*', '+', '{', '}', '(', ')', '[', ']', '|', '^', '$'), Patterns::character
            )
        );

        characterClass.is(
            anyOf(characterClassEscape, characterClassBracketed, wildcardEscape)
        );

        characterClassBracketed.is(
            sequence(
                omit(leftBracket), characterGroup, omit(rightBracket)
            )
        );

        characterGroup.is(
            transform(
                sequence(
                    optional(circumflex), oneOrMore(anyOf(characterRange, characterClassEscape))
                ),
                match -> {
                    var circumflex = match.first();
                    var rangePatterns = match.second();
                    var pattern = rangePatterns.stream().reduce((a, b) -> a.or(b)).get();
                    if (circumflex.isPresent()) {
                        return pattern.negate();
                    }
                    return pattern;
                }
            )
        );

        characterRange.is(
            anyOf(
                multiCharacterRange, singleCharacterRange
            )
        );

        multiCharacterRange.is(
            transform(
                sequence(
                    noneOf('-', '[', ']'), omit(literal('-')), noneOf('-', '[', ']')
                ),
                match -> Patterns.range(match.first(), match.second())
            )
        );

        singleCharacterRange.is(
            transform(
                noneOf('[', ']'), Patterns::character
            )
        );

        characterClassEscape.is(
            anyOf(singleCharacterEscape, multiCharacterEscape)
        );

        singleCharacterEscape.is(
            sequence(
                omit(literal('\\')),
                anyOf(
                    transform(literal('a'), () -> Patterns.character('\u0007')),
                    transform(literal('e'), () -> Patterns.character('\u001B')),
                    transform(literal('f'), () -> Patterns.character('\f')),
                    transform(literal('n'), () -> Patterns.character('\n')),
                    transform(literal('r'), () -> Patterns.character('\r')),
                    transform(literal('t'), () -> Patterns.character('\t')),
                    transform(literal('\\'), () -> Patterns.character('\\')),
                    transform(literal('|'), () -> Patterns.character('|')),
                    transform(literal('.'), () -> Patterns.character('.')),
                    transform(literal('-'), () -> Patterns.character('-')),
                    transform(literal('^'), () -> Patterns.character('^')),
                    transform(literal('$'), () -> Patterns.character('$')),
                    transform(literal('?'), () -> Patterns.character('?')),
                    transform(literal('*'), () -> Patterns.character('*')),
                    transform(literal('+'), () -> Patterns.character('+')),
                    transform(literal('{'), () -> Patterns.character('{')),
                    transform(literal('}'), () -> Patterns.character('}')),
                    transform(literal('('), () -> Patterns.character('(')),
                    transform(literal(')'), () -> Patterns.character(')')),
                    transform(literal('['), () -> Patterns.character('[')),
                    transform(literal(']'), () -> Patterns.character(']'))
                )
            )
        );

        multiCharacterEscape.is(
            sequence(
                omit(literal('\\')),
                anyOf(
                    transform(literal('d'), () -> Patterns.digit()),
                    transform(literal('D'), () -> Patterns.digit().negate()),
                    transform(literal('s'), () -> Patterns.whitespace()),
                    transform(literal('S'), () -> Patterns.whitespace().negate()),
                    transform(literal('w'), () -> Patterns.letterOrDigit()),
                    transform(literal('W'), () -> Patterns.letterOrDigit().negate())
                )
            )
        );

        wildcardEscape.is(
            transform(
                dot, () -> Patterns.any()
            )
        );

        pipe.is(literal('|'));

        question.is(literal('?'));

        star.is(literal('*'));

        plus.is(literal('+'));

        dot.is(literal('.'));

        comma.is(literal(','));

        leftBrace.is(literal('{'));

        rightBrace.is(literal('}'));

        leftParen.is(literal('('));

        rightParen.is(literal(')'));

        leftBracket.is(literal('['));

        rightBracket.is(literal(']'));

        circumflex.is(literal('^'));

        integer.is(unsignedInteger());
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
