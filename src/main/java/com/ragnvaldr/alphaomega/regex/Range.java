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

final class Range {
    private final int minimum;

    private final int maximum;

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

    public static Range between(int lowerBound, int upperBound) {
        return new Range(lowerBound, upperBound);
    }

    public static Range exact(int bound) {
        return new Range(bound, bound);
    }

    public static Range atLeast(int minimum) {
        return new Range(minimum, Integer.MAX_VALUE);
    }
}
