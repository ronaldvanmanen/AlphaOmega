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

import com.ragnvaldr.alphaomega.Scanner;

final class RepeatPattern extends Pattern {

    private Pattern pattern;
    private int lowerBound;
    private int upperBound;

    public RepeatPattern(Pattern pattern, int lowerBound, int upperBound) {
        if (lowerBound < 0 || upperBound < lowerBound) {
            throw new IllegalArgumentException("Invalid bounds");
        }
        this.pattern = pattern;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public boolean matches(Scanner scanner) {
        var position = scanner.getPosition();
        var count = 0;
        while (count < upperBound && pattern.matches(scanner)) {
            ++count;
        }
        if (count >= lowerBound && count <= upperBound) {
            return true;
        }
        scanner.setPosition(position);
        return false;
    }
}
