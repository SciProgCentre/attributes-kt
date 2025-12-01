/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A marker interface for an attribute. Attributes are used as keys to access contents of type [T] in the container.
 */
public interface Attribute<T> {
    /**
     * Stores attributes that potentially should be assigned when this attribute is assigned.
     */
    public val superattributes: List<Attribute<in T>> get() = emptyList()
}

/**
 * Returns set that consists of [this] attribute, its superattributes, their superattributes and so on.
 */
public val <T> Attribute<in T>.withSuperattributes: Set<Attribute<in T>>
    get() = buildSet {
        val attributesToCheck = mutableSetOf(this@withSuperattributes)
        while (attributesToCheck.isNotEmpty()) {
            val nextKey = attributesToCheck.first()
            attributesToCheck.remove(nextKey)
            add(nextKey)
            for (newKey in nextKey.superattributes) {
                if (newKey !in this) attributesToCheck.add(newKey)
            }
        }
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

