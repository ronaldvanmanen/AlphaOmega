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
package com.ragnvaldr.alphaomega.parsing;

import com.ragnvaldr.alphaomega.scanning.Scanner;

public final class StringParser implements Parser<String> {

    private String string;

    StringParser(String string) {
        this.string = string;
    }

    @Override
    public ParseResult<String> parse(Scanner scanner) {
        var position = scanner.getPosition();

        var builder = new StringBuilder();

        for (var index = 0; index < string.length(); ++index) {
            var character = scanner.read();
            if (character == string.charAt(index)) {
                builder.append((char)character);
            } else {
                scanner.setPosition(position);
                return ParseResult.failure();
            }
        }

        return ParseResult.success(builder.toString());
    }
}
