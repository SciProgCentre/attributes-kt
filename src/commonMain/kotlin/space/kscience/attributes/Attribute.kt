/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

public interface Attribute<T> {

    /**
     * Return an [Attributes] set that is automatically implied when this attribute is set.
     *
     * Implied attributes always have a lower priority than manual attributes. Meaning that if an attribute is implied and set,
     * the set value will be used instead of the implied one.
     *
     * If several attributes imply the same attribute with different values and no manual attribute is set,
     * the builder should produce an error.
     */
    public fun implies(value: T): Attributes? = null
}

/**
 * An attribute that could be either present or absent
 */
public interface FlagAttribute : Attribute<Unit>

/**
 * An attribute with a default value
 */
public interface AttributeWithDefault<T> : Attribute<T> {
    public val default: T
}

/**
 * Attribute containing a set of values
 */
public interface SetAttribute<V> : Attribute<Set<V>>

