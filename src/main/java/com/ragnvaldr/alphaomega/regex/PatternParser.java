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

import com.ragnvaldr.alphaomega.parsing.*;
import com.ragnvaldr.alphaomega.scanning.Scanner;
import com.ragnvaldr.alphaomega.util.Range;

import static com.ragnvaldr.alphaomega.parsing.Parse.*;

final class PatternParser implements Parser<Pattern> {

    private final Identifier<Pattern> regex = new Identifier<>();

    private final Identifier<Pattern> branch = new Identifier<>();

    private final Identifier<Pattern> piece = new Identifier<>();

    private final Identifier<Range> quantifier = new Identifier<>();

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

    private final Identifier<Pattern> captureGroup = new Identifier<>();

    private final Identifier<Character> pipe = new Identifier<>();

    private final Identifier<Character> questionMark = new Identifier<>();

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
            map(
                sequence(branch,
                    zeroOrMore(
                        sequence(
                            pipe, zeroOrOne(branch)
                        )
                    )
                ),
                match -> {
                    var firstBranch = match.first();
                    var optionalBranches = match.second();
                    var pattern = optionalBranches.stream()
                        .map(pair -> pair.second().orElseGet(Patterns::emptyString))
                        .reduce(firstBranch, (a, b) -> Patterns.anyOf(a, b));

                    return pattern;
                }
            )
        );

        branch.is(
            map(
                oneOrMore(piece), patterns -> Patterns.sequence(patterns)
            )
        );

        piece.is(
            map(
                sequence(
                    atom, zeroOrOne(quantifier)
                ),
                match -> {
                    var atom = match.first();

                    var optionalQuantifier = match.second();
                    if (!optionalQuantifier.isPresent()) {
                        return atom;
                    }

                    var range = optionalQuantifier.get();
                    var repetition = Patterns.repeat(atom, range.getMinimum(), range.getMaximum());
                    return repetition;
                }
            )
        );

        quantifier.is(
            any(
                map(questionMark, () -> Range.closed(0, 1)),
                map(star, () -> Range.atLeast(0)),
                map(plus, () -> Range.atLeast(1)),
                map(
                    sequence(
                        leftBrace, quantity, rightBrace
                    )
                    , triple -> triple.second()
                )
            )
        );

        quantity.is(
            any(quantityRange, quantityMin, quantityExact)
        );

        quantityRange.is(
            map(
                sequence(
                    integer, comma, integer
                )
                , triple -> Range.closed(triple.first(), triple.third())
            )
        );

        quantityMin.is(
            map(
                sequence(integer, comma), pair -> Range.atLeast(pair.first())
            )
        );

        quantityExact.is(
            map(integer, Range::singleton)
        );

        atom.is(
            any(
                normalCharacter,
                characterClass,
                captureGroup
            )
        );

        normalCharacter.is(
            map(
                except(
                    '.', '\\', '?', '*', '+', '{', '}', '(', ')', '[', ']', '|', '^', '$'
                )
                , Patterns::character
            )
        );

        characterClass.is(
            any(characterClassEscape, characterClassBracketed, wildcardEscape)
        );

        characterClassBracketed.is(
            map(
                sequence(
                    leftBracket, characterGroup, rightBracket
                )
                , triple -> triple.second()
            )
        );

        characterGroup.is(
            map(
                sequence(
                    zeroOrOne(circumflex), oneOrMore(any(characterRange, characterClassEscape))
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
            any(multiCharacterRange, singleCharacterRange)
        );

        multiCharacterRange.is(
            map(
                sequence(
                    except('-', '[', ']'), literal('-'), except('-', '[', ']')
                )
                , triplet -> Patterns.range(triplet.first(), triplet.third())
            )
        );

        singleCharacterRange.is(
            map(
                except('[', ']'), Patterns::character
            )
        );

        characterClassEscape.is(
            any(singleCharacterEscape, multiCharacterEscape)
        );

        singleCharacterEscape.is(
            map(
                sequence(
                    literal('\\'),
                    any(
                        map(literal('a'), () -> Patterns.character('\u0007')),
                        map(literal('e'), () -> Patterns.character('\u001B')),
                        map(literal('f'), () -> Patterns.character('\f')),
                        map(literal('n'), () -> Patterns.character('\n')),
                        map(literal('r'), () -> Patterns.character('\r')),
                        map(literal('t'), () -> Patterns.character('\t')),
                        map(literal('\\'), () -> Patterns.character('\\')),
                        map(literal('|'), () -> Patterns.character('|')),
                        map(literal('.'), () -> Patterns.character('.')),
                        map(literal('-'), () -> Patterns.character('-')),
                        map(literal('^'), () -> Patterns.character('^')),
                        map(literal('$'), () -> Patterns.character('$')),
                        map(literal('?'), () -> Patterns.character('?')),
                        map(literal('*'), () -> Patterns.character('*')),
                        map(literal('+'), () -> Patterns.character('+')),
                        map(literal('{'), () -> Patterns.character('{')),
                        map(literal('}'), () -> Patterns.character('}')),
                        map(literal('('), () -> Patterns.character('(')),
                        map(literal(')'), () -> Patterns.character(')')),
                        map(literal('['), () -> Patterns.character('[')),
                        map(literal(']'), () -> Patterns.character(']'))
                    )
                ),
                pair -> pair.second()
            )
        );

        multiCharacterEscape.is(
            map(
                sequence(
                    literal('\\'),
                    any(
                        map(literal('d'), () -> Patterns.digit()),
                        map(literal('D'), () -> Patterns.digit().negate()),
                        map(literal('s'), () -> Patterns.whitespace()),
                        map(literal('S'), () -> Patterns.whitespace().negate()),
                        map(literal('w'), () -> Patterns.letterOrDigit()),
                        map(literal('W'), () -> Patterns.letterOrDigit().negate())
                    )
                ),
                pair -> pair.second()
            )
        );

        wildcardEscape.is(
            map(
                dot, () -> Patterns.any()
            )
        );

        captureGroup.is(
            map(
                sequence(leftParen, regex, rightParen), triplet -> triplet.second()
            )
        );

        pipe.is(literal('|'));

        questionMark.is(literal('?'));

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
