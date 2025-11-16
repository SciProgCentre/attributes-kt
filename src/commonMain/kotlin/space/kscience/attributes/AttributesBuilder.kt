/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

import kotlin.jvm.JvmName

/**
 * A builder for [Attributes].
 * The builder is not thread safe
 *
 * @param O type marker of an owner object, for which these attributes are made
 */
public class AttributesBuilder<out O> {

    private val map = mutableMapOf<Attribute<*>, Any?>()

    private val content: Map<out Attribute<*>, Any?> get() = map

    /**
     * Provide an [Attributes] container created from a snapshot of the builder
     */
    public fun attributes(): Attributes = AttributesMap(HashMap(content))

    @JvmName("set")
    public fun <V> put(attribute: Attribute<V>, value: V?) {
        if (value == null) {
            map.remove(attribute)
        } else {
            map[attribute] = value
        }
    }

    public infix fun <V> Attribute<V>.put(value: V?) {
        put(this, value)
    }

    public operator fun <V> Attribute<V>.invoke(value: V?) {
        put(this, value)
    }

    /**
     * Put all attributes from given [attributes]
     */
    public fun putFrom(attributes: Attributes) {
        map.putAll(attributes.content)
    }

    @Suppress("UNCHECKED_CAST")
    public infix fun <V> SetAttribute<V>.add(attrValue: V) {
        val currentSet: Set<V> = (content[this] as? Set<V>) ?: emptySet()
        map[this] = currentSet + attrValue
    }

    /**
     * Remove an element from [SetAttribute]
     */
    @Suppress("UNCHECKED_CAST")
    public infix fun <V> SetAttribute<V>.remove(attrValue: V) {
        val currentSet: Set<V> = (content[this] as? Set<V>) ?: emptySet()
        map[this] = currentSet - attrValue
    }
}

/**
 * Create [Attributes] with a given [builder]
 * @param O the type for which attributes are built. The type is used only during compilation phase for static extension dispatch
 */
public inline fun <O> Attributes(builder: AttributesBuilder<O>.() -> Unit): Attributes =
    AttributesBuilder<O>().apply(builder).attributes()