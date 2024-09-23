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
package com.ragnvaldr.alphaomega.util;

import java.util.NoSuchElementException;

public abstract class Either<T, S> {

    public static <T, S> Either<T, S> left(T value) {
        return new Left<T, S>(value);
    }

    public static <T, S> Either<T, S> right(S value) {
        return new Right<T, S>(value);
    }

    public abstract boolean isLeft();

    public boolean isRight() {
        return !isLeft();
    }

    public abstract T getLeft();

    public abstract S getRight();

    public static <T> T getLeftOrRight(Either<T, T> value) {
        if (value.isLeft()) {
            return value.getLeft();
        } else {
            return value.getRight();
        }
    }
}

final class Left<T, S> extends Either<T, S> {

    private final T value;

    Left(T value) {
        this.value = value;
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public T getLeft() {
        return value;
    }

    @Override
    public S getRight() {
        throw new NoSuchElementException("No value present");
    }
}

final class Right<T, S> extends Either<T, S> {

    private final S value;

    Right(S value) {
        this.value = value;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public T getLeft() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public S getRight() {
        return value;
    }
}
