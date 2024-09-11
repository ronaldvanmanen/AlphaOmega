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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PatternTests {

    @Test
    void parseAnyCharacter() {
        var pattern = Pattern.parse(".");
        assertFalse(pattern.matches("\n"));
        chars().filter(s -> s == "\n").forEach(c -> {
            assertTrue(pattern.matches(c));
        });
    }

    @Test
    void parseLiteralCharacter() {
        var pattern = Pattern.parse("a");
        assertTrue(pattern.matches("a"));
    }

    @Test
    void parseNewlineCharacter() {
        var pattern = Pattern.parse("\\n");
        assertTrue(pattern.matches("\n"));
        chars().filter(s -> s == "\n").forEach(c -> {
            assertFalse(pattern.matches(c));
        });
    }

    Stream<String> chars() {
        return IntStream
            .range(Character.MIN_VALUE, Character.MAX_VALUE)
            .mapToObj(value -> String.valueOf((char) value));
    }
}