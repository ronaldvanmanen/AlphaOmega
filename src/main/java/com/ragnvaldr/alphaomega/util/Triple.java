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

/**
 * The {@link Triple} class represents an ordered triplet of objects.
 */
public final record Triple<T1, T2, T3>(T1 first, T2 second, T3 third) {

    /**
     * Creates a {@link Triple} with the specified values.
     *
     * @param <T1> The type of the first value.
     * @param <T2> The type of the second value.
     * @param <T3> The type of the third value.
     * @param first The first value.
     * @param second The second value.
     * @param third The third value.
     * @return A {@link Triple} with the specified values.
     */
    public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 first, T2 second, T3 third) {
        return new Triple<>(first, second, third);
    }

    public static <T1, T2, T3> Triple<T1, T2, T3> of(T1 first, Pair<T2, T3> secondAndThird) {
        return new Triple<>(first, secondAndThird.first(), secondAndThird.second());
    }

}
