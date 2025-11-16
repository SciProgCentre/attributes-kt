/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A container for [Attributes]
 */
public interface AttributeContainer {
    public val attributes: Attributes
}

/**
 * A container with mutable set of attributes
 */
@UnstableAPI
public interface MutableAttributeContainer : AttributeContainer {
    override var attributes: Attributes
}

@UnstableAPI
public inline fun <O : MutableAttributeContainer> O.attributes(builder: AttributesBuilder<O>.() -> Unit) {
    attributes = attributes.modified(builder)
}
