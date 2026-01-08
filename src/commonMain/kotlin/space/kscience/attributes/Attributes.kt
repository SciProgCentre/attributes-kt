/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A set of attributes. The implementation must guarantee that [content] keys correspond to their value types.
 *
 * The following contracts are in place for this class:
 * * the type of item provided is always the same as the type-parameter of the attribute;
 * * content composition immutable (the same objects always correspond to the same keys, not guarantees about the content);
 * * structural equality.
 *
 * It is not recommended to implement this interface in application level code to avoid breaking its contract.
 *
 * Attribute implications conflicts are checked during construction, so constructing [Attributes] could produce an error.
 */
@SubclassOptInRequired(UnsafeAPI::class)
public interface Attributes {
    /**
     * Raw (explicit) content for this [Attributes]
     */
    public val content: Map<out Attribute<*>, Any?>

    /**
     * Only implied values without explicit values.
     *
     * This property is mostly used for optimization purposes.
     */
    public val implied: Map<out Attribute<*>, Any?>

    /**
     * Explicit attribute keys contained in this [Attributes]
     */
    public val keys: Set<Attribute<*>> get() = content.keys

    /**
     * Provide an attribute value or a value implied by a present value via [Attribute.implies].
     *
     * Return null if an attribute is not present or if its value is null.
     */
    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(attribute: Attribute<out T>): T?

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    public companion object {
        public val EMPTY: Attributes = object : Attributes {
            override val content: Map<out Attribute<*>, Any?> get() = emptyMap()

            override val implied: Map<out Attribute<*>, Any?> get() = emptyMap()

            override fun <T> get(attribute: Attribute<out T>): T? = null

            override fun toString(): String = "Attributes.EMPTY"

            override fun equals(other: Any?): Boolean = (other as? Attributes)?.isEmpty() ?: false

            override fun hashCode(): Int = Unit.hashCode()
        }

        /**
         * Check the structural equality of two attribute sets.
         */
        public fun equals(a1: Attributes, a2: Attributes): Boolean =
            a1.keys == a2.keys && a1.keys.all { a1[it] == a2[it] }

        /**
         * Create [Attributes] from a given [content] map.
         * This method does not check the validity of the provided map, so it should be used with care.
         *
         * User **must** ensure that each key contains a value of the same type as referenced by the key.
         */
        @UnsafeAPI
        public fun unsafe(content: Map<out Attribute<*>, Any?>): Attributes = AttributesMap(content)
    }
}

/**
 * Implementation of attributes based on a read-only [Map]
 */
@OptIn(UnsafeAPI::class)
@PublishedApi
internal class AttributesMap(override val content: Map<out Attribute<*>, Any?>) : Attributes {

    override val implied = buildMap {
        content.forEach { (key, value) ->
            @Suppress("UNCHECKED_CAST")
            val impliedPairs = (key as Attribute<Any?>).implies(value) ?: return@forEach

            (impliedPairs.content + impliedPairs.implied).forEach { (impliedKey, impliedValue) ->
                when (impliedKey) {
                    in content -> Unit

                    in this if get(impliedKey) != impliedValue ->
                        error("Attribute $key is implied by multiple attributes with different values. Please resolve the conflict by providing explicit value.")

                    else -> put(impliedKey, impliedValue)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(attribute: Attribute<out T>): T? = (content[attribute] ?: implied[attribute]) as? T?

    override fun toString(): String = "Attributes(value=${content.entries})"
    override fun equals(other: Any?): Boolean = other is Attributes && Attributes.equals(this, other)
    override fun hashCode(): Int = content.hashCode()
}

public fun Attributes.isEmpty(): Boolean = keys.isEmpty()

/**
 * Get attribute value or default
 */
public fun <T> Attributes.getOrDefault(attribute: AttributeWithDefault<T>): T = get(attribute) ?: attribute.default

/**
 * Check if there is an attribute that matches given key by type and adheres to [predicate].
 */
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified A : Attribute<T>> Attributes.hasAny(predicate: (value: T) -> Boolean): Boolean =
    content.any { (mapKey, mapValue) -> mapKey is A && predicate(mapValue as T) }

/**
 * Check if there is an attribute of given type (subtypes included)
 */
public inline fun <reified A : Attribute<*>> Attributes.hasAny(): Boolean =
    content.any { (mapKey, _) -> mapKey is A }

/**
 * Check if [Attributes] contains a flag. Multiple keys that are instances of a flag could be present
 */
public inline fun <reified A : FlagAttribute> Attributes.hasFlag(): Boolean =
    content.keys.any { it is A }

/**
 * Create [Attributes] with an added or replaced attribute key.
 */
public fun <T, A : Attribute<T>> Attributes.withAttribute(
    attribute: A,
    attrValue: T,
): Attributes = AttributesMap(content + (attribute to attrValue))

/**
 * Create [Attributes] with additional flag attribute
 */
public fun <A : Attribute<Unit>> Attributes.withFlag(attribute: A): Attributes =
    withAttribute(attribute, Unit)

/**
 * Create a new [Attributes] by modifying the current one.
 */
public inline fun <O> Attributes.modified(block: AttributesBuilder<O>.() -> Unit): Attributes = Attributes<O> {
    putFrom(this@modified)
    block()
}

/**
 * Create new [Attributes] by removing [attribute] key if it is present
 */
public fun Attributes.withoutAttribute(attribute: Attribute<*>): Attributes =
    AttributesMap(content.minus(attribute))

/**
 * Add an element to a [SetAttribute]
 */
public fun <T, A : SetAttribute<T>> Attributes.withAttributeElement(
    attribute: A,
    attrValue: T,
): Attributes {
    val currentSet: Set<T> = get(attribute) ?: emptySet()
    return AttributesMap(
        content + (attribute to (currentSet + attrValue))
    )
}

/**
 * Remove an element from [SetAttribute]
 */
public fun <T, A : SetAttribute<T>> Attributes.withoutAttributeElement(
    attribute: A,
    attrValue: T,
): Attributes {
    val currentSet: Set<T> = get(attribute) ?: emptySet()
    return AttributesMap(content + (attribute to (currentSet - attrValue)))
}

/**
 * Create [Attributes] with a single key-value pair
 */
public fun <T, A : Attribute<T>> Attributes(
    attribute: A,
    attrValue: T,
): Attributes = AttributesMap(mapOf(attribute to attrValue))

/**
 * Create Attributes with a single flag attribute
 */
public fun <A : Attribute<Unit>> Attributes(
    attribute: A,
): Attributes = AttributesMap(mapOf(attribute to Unit))

/**
 * Create a new [Attributes] that overlays [other] on top of this set of attributes. New attributes are added.
 * Existing attribute keys are replaced.
 */
public operator fun Attributes.plus(other: Attributes): Attributes = when {
    isEmpty() -> other
    other.isEmpty() -> this
    else -> AttributesMap(content + other.content)
}

/**
 * Create a new [Attributes] with removed [key] (if it is present).
 */
public operator fun Attributes.minus(key: Attribute<*>): Attributes =
    if (content.contains(key)) AttributesMap(content.minus(key)) else this