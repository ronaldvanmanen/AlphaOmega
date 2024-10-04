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

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.ragnvaldr.alphaomega.util.Streams.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
final class PatternTests {

    @Test
    void matchesAnyCharacter() {
        var pattern = Pattern.parse(".");
        chars().forEach(c -> {
            assertEquals(
                c != '\n', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchAnyCharacter() {
        var pattern = Pattern.parse(".");
        chars().forEach(c -> {
            var input = String.valueOf(c);
            var matchResult = pattern.match(input);
            if (c != '\n') {
                assertTrue(matchResult.isSuccess());
                assertFalse(matchResult.isFailure());
                assertEquals(input, matchResult.getValue());
            } else {
                assertFalse(matchResult.isSuccess());
                assertTrue(matchResult.isFailure());
                assertThrows(NoSuchElementException.class, () -> matchResult.getValue());
            }
        });
    }

    @Test
    void matchNormalCharacter() {
        var pattern = Pattern.parse("a");
        chars().forEach(c -> {
            assertEquals(
                c == 'a', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchBell() {
        var pattern = Pattern.parse("\\a");
        chars().forEach(c -> {
            assertEquals(
                c == '\u0007', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchEscape() {
        var pattern = Pattern.parse("\\e");
        chars().forEach(c -> {
            assertEquals(
                c == '\u001B', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchNewline() {
        var pattern = Pattern.parse("\\n");
        chars().forEach(c -> {
            assertEquals(
                c == '\n', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesCarriageReturn() {
        var pattern = Pattern.parse("\\r");
        chars().forEach(c -> {
            assertEquals(
                c == '\r', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesHorizontalTabulation() {
        var pattern = Pattern.parse("\\t");
        chars().forEach(c -> {
            assertEquals(
                c == '\t', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesDigit() {
        var pattern = Pattern.parse("\\d");
        chars().forEach(c -> {
            assertEquals(
                Character.isDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesNonDigit() {
        var pattern = Pattern.parse("\\D");
        chars().forEach(c -> {
            assertEquals(
                !Character.isDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesWhitespace() {
        var pattern = Pattern.parse("\\s");
        chars().forEach(c -> {
            assertEquals(
                Character.isWhitespace(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesNonWhitespace() {
        var pattern = Pattern.parse("\\S");
        chars().forEach(c -> {
            assertEquals(
                !Character.isWhitespace(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesWord() {
        var pattern = Pattern.parse("\\w");
        chars().forEach(c -> {
            assertEquals(
                Character.isLetterOrDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesNonWord() {
        var pattern = Pattern.parse("\\W");
        chars().forEach(c -> {
            assertEquals(
                !Character.isLetterOrDigit(c), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesSinglePositiveCharacterRange() {
        var pattern = Pattern.parse("[0-9]");
        chars().forEach(c -> {
            assertEquals(
                c >= '0' && c <= '9', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesMultiplePositiveCharacterRange() {
        var pattern = Pattern.parse("[0-9a-zA-Z]");
        chars().forEach(c -> {
            assertEquals(
                (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z'), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesNegativeCharacterRange() {
        var pattern = Pattern.parse("[^0-9]");
        chars().forEach(c -> {
            assertEquals(
                c < '0' || c > '9', pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesMultipleNegativeCharacterRange() {
        var pattern = Pattern.parse("[^0-9a-zA-Z]");
        chars().forEach(c -> {
            assertEquals(
                (c < '0' || c > '9') &&
                (c < 'a' || c > 'z') &&
                (c < 'A' || c > 'Z'), pattern.matches(String.valueOf(c))
            );
        });
    }

    @Test
    void matchesAlternative() {
        var pattern = Pattern.parse("cat|cataract|caterpillar");
        assertTrue(pattern.matches("cat"));
        assertTrue(pattern.matches("cataract"));
        assertTrue(pattern.matches("caterpillar"));
    }

    @Test
    void matchesGroup() {
        var pattern = Pattern.parse("cat(aract|erpillar|)");
        assertTrue(pattern.matches("cat"));
        assertTrue(pattern.matches("cataract"));
        assertTrue(pattern.matches("caterpillar"));
    }
}
