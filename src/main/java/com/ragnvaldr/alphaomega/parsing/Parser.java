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

/**
 * Defines the requirements for all parsers.
 *
 * @param <T> The type of the object returned by a parser on a successfull match.
 */
public interface Parser<T> {

    /**
     * Parse the characters provided by {@code scanner} as an object of type {@code <T>}.
     *
     * @param scanner A {@link Scanner} providing characters to be parsed.
     *
     * @return A {@link ParseResult.Success} containing the parsed object on a
     * successfull match, otherwise a {@link ParseResult.Failure} if the parse
     * failed.
     */
    public ParseResult<T> parse(Scanner scanner);

}
