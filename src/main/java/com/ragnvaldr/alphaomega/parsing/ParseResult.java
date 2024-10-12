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

/**
 * The {@link ParseResult} class captures the result of a parser.
 */
public abstract class ParseResult<T> {

    static final class Success<T> extends ParseResult<T> {

        private T value;

        public Success() { }

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    static final class Failure<T> extends ParseResult<T> {

        public Failure() { }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T getValue() {
            throw new NoSuchElementException("No value present");
        }
    }

    ParseResult() { }

    /**
     * Creates a {@link ParseResult} that indicates that successfull match.
     *
     * @param <T> The type of value that was parsed by a parser.
     *
     * @return A {@link ParseResult} with the specified value.
     */
    public static <T> ParseResult<T> success() {
        return new Success<>();
    }

    /**
     * Creates a {@link ParseResult} that indicates that a parser successfully
     * matched the specified value.
     *
     * @param <T> The type of value that was parsed by a parser.
     * @param value The value that was parsed by a parser.
     * @return A {@link ParseResult} with the specified value.
     */
    public static <T> ParseResult<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a {@link ParseResult} that indicates that a parser unsuccessfully matched.
     */
    public static <T> ParseResult<T> failure() {
        return new Failure<>();
    }

    /**
     * Returns whether a parser successfully matched.
     *
     * @return true if the parser successfully matched; false, otherwise.
     */
    public abstract boolean isSuccess();

    /**
     * Returns whether a parser failed to match.
     *
     * @return true if the parser failed to matched; false, otherwise.
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the parsed value, if any.
     *
     * @return The parsed value, or {@code null} if no value was parsed.
     *
     * @throws NoSuchElementException No value is present because the parse was unsuccessfull.
     */
    public abstract T getValue();
}
