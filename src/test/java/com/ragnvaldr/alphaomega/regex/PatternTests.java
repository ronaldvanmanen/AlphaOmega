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

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import static com.ragnvaldr.alphaomega.util.Streams.*;

@SpringBootTest
final class PatternTests {

    @Test
    void parseAnyCharacter() {
        var pattern = Pattern.parse(".");
        chars().forEach(c -> {
            assertEquals(
                c != '\n', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseNormalCharacter() {
        var pattern = Pattern.parse("a");
        chars().forEach(c -> {
            assertEquals(
                c == 'a', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseBell() {
        var pattern = Pattern.parse("\\a");
        chars().forEach(c -> {
            assertEquals(
                c == '\u0007', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseEscape() {
        var pattern = Pattern.parse("\\e");
        chars().forEach(c -> {
            assertEquals(
                c == '\u001B', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseNewline() {
        var pattern = Pattern.parse("\\n");
        chars().forEach(c -> {
            assertEquals(
                c == '\n', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseCarriageReturn() {
        var pattern = Pattern.parse("\\r");
        chars().forEach(c -> {
            assertEquals(
                c == '\r', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseHorizontalTabulation() {
        var pattern = Pattern.parse("\\t");
        chars().forEach(c -> {
            assertEquals(
                c == '\t', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseDigit() {
        var pattern = Pattern.parse("\\d");
        chars().forEach(c -> {
            assertEquals(
                Character.isDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseNonDigit() {
        var pattern = Pattern.parse("\\D");
        chars().forEach(c -> {
            assertEquals(
                !Character.isDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseWhitespace() {
        var pattern = Pattern.parse("\\s");
        chars().forEach(c -> {
            assertEquals(
                Character.isWhitespace(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseNonWhitespace() {
        var pattern = Pattern.parse("\\S");
        chars().forEach(c -> {
            assertEquals(
                !Character.isWhitespace(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parsePositiveCharacterRange() {
        var pattern = Pattern.parse("[0-9]");
        chars().forEach(c -> {
            assertEquals(
                c >= '0' && c <= '9', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseNegativeCharacterRange() {
        var pattern = Pattern.parse("[^0-9]");
        chars().forEach(c -> {
            assertEquals(
                c < '0' || c > '9', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void parseAlternative() {
        var pattern = Pattern.parse("cat|cataract|caterpillar");
        assertTrue(pattern.matches("cat"));
        assertTrue(pattern.matches("cataract"));
        assertTrue(pattern.matches("caterpillar"));
    }

    @Test
    void parseGroup() {
        var pattern = Pattern.parse("cat(aract|erpillar|)");
        assertTrue(pattern.matches("cat"));
        assertTrue(pattern.matches("cataract"));
        assertTrue(pattern.matches("caterpillar"));
    }
}