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

import java.util.stream.*;
import java.util.stream.Collectors;

import com.ragnvaldr.alphaomega.*;
import com.ragnvaldr.alphaomega.parsers.*;
import com.ragnvaldr.alphaomega.util.*;

import static com.ragnvaldr.alphaomega.parsers.Parsers.*;

final class PatternParser implements Parser<Pattern> {

    private final Rule<Pattern> regex = new Rule<>();

    private final Rule<Pattern> alternation = new Rule<>();

    private final Rule<Pattern> branch = new Rule<>();

    private final Rule<Pattern> piece = new Rule<>();

    private final Rule<Quantifier> quantifier = new Rule<>();

    private final Rule<Range> quantity = new Rule<>();

    private final Rule<Range> quantityRange = new Rule<>();

    private final Rule<Range> quantityMinRange = new Rule<>();

    private final Rule<Range> quantityExact = new Rule<>();

    private final Rule<Pattern> atom = new Rule<>();

    private final Rule<Pattern> normalCharacter = new Rule<>();

    private final Rule<Pattern> escapedCharacter = new Rule<>();

    private final Rule<Pattern> characterType = new Rule<>();

    private final Rule<Pattern> characterClass = new Rule<>();

    private final Rule<Pattern> characterGroup = new Rule<>();

    private final Rule<CharacterPattern> characterRange = new Rule<>();

    private final Rule<Character> character = new Rule<>();

    public PatternParser() {

        regex.is(
            transform(
                optional(alternation), pattern -> pattern.orElseGet(Patterns::emptyString)
            )
        );

        alternation.is(
            transform(
                sequence(branch,
                    zeroOrMore(
                        transform(
                            sequence(literal('|'), optional(branch)), Pair::second
                        )
                    )
                ),
                match -> {
                    var head = match.first();
                    var tail = match.second();
                    if (tail.size() == 0) {
                        return head;
                    }

                    var patterns = Stream.concat(
                        Stream.of(head), tail.stream().map(p -> p.orElseGet(Patterns::emptyString))
                    )
                    .collect(Collectors.toList());

                    return Patterns.oneOf(patterns);
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
            oneOf(
                transform(literal('?'), Quantifier::optional),
                transform(literal('*'), Quantifier::zeroOrMore),
                transform(literal('+'), Quantifier::oneOrMore),
                transform(
                    sequence(
                        literal('{'), quantity, literal('}')
                    ),
                    match -> Quantifier.range(match.second())
                )
            )
        );

        quantity.is(
            oneOf(quantityRange, quantityMinRange, quantityExact)
        );

        quantityRange.is(
            transform(
                sequence(
                    unsignedInteger(), literal(','), unsignedInteger()
                ),
                match -> Range.between(match.first(), match.third())
            )
        );

        quantityMinRange.is(
            transform(
                sequence(
                    unsignedInteger(), literal(',')
                ),
                match -> Range.atLeast(match.first())
            )
        );

        quantityExact.is(
            transform(
                unsignedInteger(), Range::exact
            )
        );

        atom.is(
            oneOf(
                normalCharacter,
                escapedCharacter,
                characterType,
                characterClass,
                transform(
                    sequence(
                        literal('('), regex, literal(')')
                    ),
                    Triple::second
                )
            )
        );

        normalCharacter.is(
            transform(character, Patterns::character)
        );

        escapedCharacter.is(
            transform(
                sequence(
                    literal('\\'),
                    oneOf(
                        transform(literal('a'), _ -> Patterns.character('\u0007')),
                        transform(literal('e'), _ -> Patterns.character('\u001B')),
                        transform(literal('f'), _ -> Patterns.character('\f')),
                        transform(literal('n'), _ -> Patterns.character('\n')),
                        transform(literal('r'), _ -> Patterns.character('\r')),
                        transform(literal('t'), _ -> Patterns.character('\t'))
                    )
                ),
                Pair::second
            )
        );

        characterType.is(
            oneOf(
                transform(literal('.'), _ -> Patterns.any()),
                transform(
                    sequence(
                        literal('\\'),
                        oneOf(
                            transform(literal('d'), _ -> Patterns.digit()),
                            transform(literal('D'), _ -> Patterns.digit().negate()),
                            transform(literal('s'), _ -> Patterns.whitespace()),
                            transform(literal('S'), _ -> Patterns.whitespace().negate())
                        )
                    ),
                    Pair::second
                )
            )
        );

        characterClass.is(
            transform(
                sequence(
                    literal('['), characterGroup, literal(']')
                ),
                Triple::second
            )
        );

        characterGroup.is(
            transform(
                sequence(
                    optional(literal('^')), characterRange
                ),
                match -> {
                    var pattern = match.second();
                    var negated = match.first();
                    if (negated.isPresent()) {
                        return pattern.negate();
                    }
                    return pattern;
                }
            )
        );

        characterRange.is(
            transform(
                sequence(
                    character, literal('-'), character
                ),
                match -> Patterns.range(match.first(), match.third())
            )
        );

        character.is(
            notOneOf('\\', '^', '$', '.', '[', ']', '|', '(', ')', '?', '*', '+', '{', '}')
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
