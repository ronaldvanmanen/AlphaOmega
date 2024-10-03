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

import java.util.NoSuchElementException;

import com.ragnvaldr.alphaomega.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
final class StringParserTests {

    @Test
    void parseSucceeds() {
        var scanner = new Scanner("Hello, World!");
        var initialPosition = scanner.getPosition();
        var parser = new StringParser("Hello");

        var parseResult = parser.parse(scanner);

        assertTrue(parseResult.isSuccess());
        assertEquals("Hello", parseResult.getValue());
        assertEquals(5, scanner.getPosition() - initialPosition);
    }

    @Test
    void parseFails() {
        var scanner = new Scanner("Hello, World!");
        var initialPosition = scanner.getPosition();
        var parser = new StringParser("World!");

        var parseResult = parser.parse(scanner);

        assertTrue(parseResult.isFailure());
        assertThrows(NoSuchElementException.class, () -> parseResult.getValue());
        assertEquals(0, scanner.getPosition() - initialPosition);
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
