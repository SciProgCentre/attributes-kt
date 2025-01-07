![Maven Central Version](https://img.shields.io/maven-central/v/space.kscience/attributes-kt)


# Attributes-kt

This is a small multiplatform library dedicated to typed attribute containers inspired by kotlinx-coroutines contexts.

`Attribute` is a marker interface for an attribute key.

`Attributes` is a generic immutable container for key-value pairs.

`SafeType` is a compile-type checked wrapper for KType.

## Access attributes

The attribute access is done in the following way:

```kotlin
object MyAttribute: Attribute<Int>

val value: Int? = attributes[MyAttribute]
```

One could also add shortcuts to attribute container for convenience:

```kotlin
class MyContainer: AttributeContainer

val MyContainer.myAttribute: Int? get() = attributes[MyAttribute]
```

While `Attributes` class itself is a container that could hold any kind of attributes, its parent container (usually `AttributeContainer`)could expose only specific attributes via extensions. 

## Create attributes

`Attributes` is created with a function with the following signature:

```kotlin
fun <O> Attributes(builder: AttributesBuilder<O>.() -> Unit): Attributes
```

`AttributeBuilder` is a mutable container that exists only during attribute configuration. One could do the following operations with it:

* put values into it via key (new values override existing values with the same key) via `put` operation (prefix or infix notation)
* add or remove an element from `SetAttribute`

```kotlin
val attributes = Attributes{
    put(MyAttribute, 3)
    MyAttribute put null
    
    MyAttribute(4)
}
```

The last notation (invoke-based) is added for convenience.

The type-parameter of the builder is used only in compile time to provide more extension points. ForExample one could provide extensions for builder like this:

```kotlin
fun AttributesBuilder<MyContainer>.defaultMyAttribute() = put(MyAttribute, 0)
```

## Modify attributes

`Attributes` are immutable, so to modify attributes one needs to copy them. There are several methods to do that like:

* `withAttribute` - create a copy with additional (or overriding) attribute;
* `withFlag` - the same for flag (value-less) attribute;
* `withAttributeElement`/`withoutAttributeElement` - add or remove an element from set-valued attribute;
* `plus` - compose (overlay) two attributes sets.

Other modifications could be added as extensions.

Generic modifications could be done via

```kotlin
fun <O> Attributes.modified(block: AttributesBuilder<O>.() -> Unit): Attributes
```

## Polymorphic attributes

Polymorphic attributes (attributes, where value has a type parameter) are more complicated. They could not be used as singleton keys since each key has a different parameter. Users may want to provide a key factory instead of keys themselves so they could organize key caching and avoid creating keys on each access:

```kotlin
class Determinant<T>(type: SafeType<T>) :
    PolymorphicAttribute<T>(type),
    MatrixAttribute<T>

val <T> MatrixScope<T>.Determinant: Determinant<T> get() = Determinant(type)

with(matrixScope) {
    val determinant = matrix[Determinant]
}
```

It is possible to simplify the work with polymorphic attributes by implementing type erasure for type arguments, but it will violate structural equality guarantees. Polymorphic attributes are work in progress and could change in the future.

${artifact}