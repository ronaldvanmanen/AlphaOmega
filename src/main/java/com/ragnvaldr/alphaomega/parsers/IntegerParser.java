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

package com.ragnvaldr.alphaomega.parsers;

import com.ragnvaldr.alphaomega.Scanner;

final class IntegerParser implements Parser<Integer> {

    private boolean signed;

    private int radix;

    private int minDigits;

    private int maxDigits;

    public IntegerParser()
    {
        this(true, 10, 1, Integer.MAX_VALUE);
    }

    public IntegerParser(boolean signed)
    {
        this(signed, 10, 1, Integer.MAX_VALUE);
    }

    public IntegerParser(int radix)
    {
        this(true, radix, 1, Integer.MAX_VALUE);
    }

    public IntegerParser(boolean signed, int radix)
    {
        this(signed, radix, 1, Integer.MAX_VALUE);
    }

    public IntegerParser(boolean signed, int radix, int minDigits, int maxDigits)
    {
        if (radix < 2 || radix > 36)
            throw new IllegalArgumentException(
                "The radix must lie between 2 and 36.");
        if (minDigits < 0)
            throw new IllegalArgumentException(
                "The minimum number of digits must greater than zero");
        if (maxDigits < 0)
            throw new IllegalArgumentException(
                "The maximum number of digits must greater than zero");
        if (minDigits > maxDigits)
            throw new IllegalArgumentException(
                "The minimum number of digits must be less than " +
                "or equal to the maximum number of digits.");

        this.signed = signed;
        this.radix = radix;
        this.minDigits = minDigits;
        this.maxDigits = maxDigits;
    }

    @Override
    public ParseResult<Integer> parse(Scanner scanner) {
        int position = scanner.getPosition();

        boolean isSigned = false;
        if (signed) {
            var character = scanner.peek();
            if ((character == '-') || (character == '+')) {
                scanner.read();
            }
            isSigned = character == '-';
        }

        int value = 0;
        int numDigits = 0;
        for (/*...*/; numDigits < maxDigits; ++numDigits)
        {
            char chr = (char)scanner.read();
            if (chr == -1 || !isDigit(chr)){
                break;
            }
            var digit = ToDigit(chr);

            value = value * radix + digit;
        }

        if (isSigned) {
            value = -value;
        }

        if (numDigits >= minDigits && numDigits < maxDigits) {
            return ParseResult.success(value);
        }

        scanner.setPosition(position);

        return ParseResult.failure();
    }

    private int ToDigit(char character) {
        if (character >= '0' && character <= '9') {
            return character - '0';
        } else {
            return 10 + character - 'a';
        }
    }

    private boolean isDigit(char character) {
        if (radix <= 10) {
            return (character >= '0' && character < '0' + radix);
        } else {
            return (character >= '0' && character <= '9')
                || (character >= 'a' && character < 'a' + radix - 10)
                || (character >= 'A' && character < 'A' + radix - 10);
        }
    }
}
