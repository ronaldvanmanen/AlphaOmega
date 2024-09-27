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

import com.ragnvaldr.alphaomega.util.Range;

import org.springframework.lang.Nullable;

final class Quantifier {
    private final QuantifierType type;

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

    public Range getRange() {
        if (range == null) {
            throw new IllegalStateException("Quantifier is not a range");
        }
        return range;
    }

    public static Quantifier optional() {
        return new Quantifier(QuantifierType.OPTIONAL);
    }

    public static Quantifier zeroOrMore() {
        return new Quantifier(QuantifierType.ZERO_OR_MORE);
    }

    public static Quantifier oneOrMore() {
        return new Quantifier(QuantifierType.ONE_OR_MORE);
    }

    public static Quantifier range(Range range) {
        return new Quantifier(QuantifierType.RANGE, range);
    }
}
