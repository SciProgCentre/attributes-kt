package space.kscience.attributes

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ImpliedAttributes {

    object AttributeA : Attribute<String>{
        override fun implies(value: String): Attributes = Attributes(AttributeB,value)
    }

    object AttributeB : Attribute<String>{
        override fun implies(value: String): Attributes = Attributes(AttributeC,value)
    }

    object AttributeC : Attribute<String>

    object AttributeD : Attribute<String>{
        override fun implies(value: String): Attributes = Attributes(AttributeC,value)
    }


    @Test
    fun testConflict(){
        assertFailsWith<IllegalStateException> {
            Attributes<Any> {
                AttributeA put "A"
                AttributeD put "D"
            }
        }
    }


    @Test
    fun testNoConflict(){
        Attributes<Any>{
            AttributeA put "A"
            AttributeD put "D"
            AttributeC put "C"
        }
    }

}