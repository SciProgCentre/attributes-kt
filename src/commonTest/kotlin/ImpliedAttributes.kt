package space.kscience.attributes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ImpliedAttributes {

    object AttributeA : Attribute<String> {
        override fun implies(value: String): Attributes = Attributes(AttributeB, value)
    }

    object AttributeB : Attribute<String> {
        override fun implies(value: String): Attributes = Attributes(AttributeC, value)
    }

    object AttributeC : Attribute<String>

    object AttributeD : Attribute<String> {
        override fun implies(value: String): Attributes = Attributes(AttributeC, value)
    }


    @Test
    fun testConflict() {
        assertFailsWith<IllegalStateException> {
            Attributes<Any> {
                AttributeA put "A"
                AttributeD put "D"
            }
        }
    }


    @Test
    fun testNoConflict() {
        val attributes = Attributes<Any> {
            AttributeA put "A"
            AttributeD put "D"
            AttributeC put "C"
        }
        assertEquals("C", attributes[AttributeC])
    }

    @Test
    fun testNoConflict2() {
        val attributes = Attributes<Any> {
            AttributeA put "C"
            AttributeD put "C"
        }

        assertEquals("C", attributes[AttributeC])
    }

    @Test
    fun testImplied() {
        val attributes = Attributes<Any> {
            AttributeB put "B"
        }
        assertEquals("B", attributes[AttributeC])
    }

}