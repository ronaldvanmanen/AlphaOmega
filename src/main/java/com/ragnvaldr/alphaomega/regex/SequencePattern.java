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

import java.util.List;

import com.ragnvaldr.alphaomega.Scanner;

final class SequencePattern extends Pattern {

    private List<Pattern> patterns;

    public SequencePattern(Pattern... patterns) {
        this(List.of(patterns));
    }

    public SequencePattern(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public MatchResult match(Scanner scanner) {
        var position = scanner.getPosition();
        var stringBuilder = new StringBuilder();
        for (var pattern : patterns) {
            var matchResult = pattern.match(scanner);
            if (matchResult.isSuccess()) {
                stringBuilder.append(matchResult.getValue());
            }
            else {
                scanner.setPosition(position);
                return MatchResult.failure();
            }
        }
        return MatchResult.success(stringBuilder.toString());
    }
}
