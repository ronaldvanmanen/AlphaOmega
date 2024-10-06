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

import java.util.List;
import java.util.NoSuchElementException;

import com.ragnvaldr.alphaomega.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
final class StringParserTests {

    final String source = "Hello, World!";

    final List<String> matchingStrings = List.of(
        "",
        "H",
        "He",
        "Hel",
        "Hell",
        "Hello",
        "Hello,",
        "Hello, ",
        "Hello, W",
        "Hello, Wo",
        "Hello, Wor",
        "Hello, Worl",
        "Hello, World",
        "Hello, World!"
    );

    @Property
    void parseSucceeds(@ForAll("parseSucceedsFor") String value) {
        var scanner = new Scanner(source);
        var initialPosition = scanner.getPosition();
        var parser = new StringParser(value);

        var parseResult = parser.parse(scanner);

        assertTrue(parseResult.isSuccess());
        assertEquals(value, parseResult.getValue());
        assertEquals(value.length(), scanner.getPosition() - initialPosition);
    }

    @Provide
    Arbitrary<String> parseSucceedsFor() {
        return Arbitraries.of(matchingStrings);
    }

    @Property
    void parseFails(@ForAll("parseFailsFor") String value) {
        var scanner = new Scanner(source);
        var initialPosition = scanner.getPosition();
        var parser = new StringParser(value);

        var parseResult = parser.parse(scanner);

        assertTrue(parseResult.isFailure());
        assertThrows(NoSuchElementException.class, () -> parseResult.getValue());
        assertEquals(0, scanner.getPosition() - initialPosition);
    }

    @Provide
    Arbitrary<String> parseFailsFor() {
        return Arbitraries.strings().filter(z -> !matchingStrings.contains(z));
    }

    @Test
    void parseFailsWhenEOF() {
        var scanner = new Scanner("");
        var initialPosition = scanner.getPosition();
        var parser = new StringParser("Hello, World!");

        var parseResult = parser.parse(scanner);

        assertTrue(parseResult.isFailure());
        assertThrows(NoSuchElementException.class, () -> parseResult.getValue());
        assertEquals(0, scanner.getPosition() - initialPosition);
    }

}
