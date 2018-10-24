package sdoward.com.dosser

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DosserTest {

    @Test
    fun shouldGenerateString() {
        data class StringClass(val theString: String)

        val stringClass = generate(StringClass::class)

        assertThat(stringClass.theString).isNotBlank()
    }

    @Test
    fun shouldGenerateNumbers() {
        data class NumberClass(val int: Int, val long: Long, val double: Double)

        val numberClass = generate(NumberClass::class)

        println(numberClass)
        assertThat(numberClass.int).isNotNull()
        assertThat(numberClass.long).isNotNull()
        assertThat(numberClass.double).isNotNull()
    }

    @Test
    fun shouldGenerateHeirachialClass() {
        data class InnerClass(val string: String, val int: Int)
        data class OutterClass(val innerClass: InnerClass)

        val outterClass = generate(OutterClass::class)

        println(outterClass)
        assertThat(outterClass.innerClass).isNotNull()
        assertThat(outterClass.innerClass.string).isNotBlank()
    }

    @Test
    fun shouldGenerateBasicTypeListClass() {
        data class BasicListTypeClass(val list: List<Int>)

        val basicListTypeClass = generate(BasicListTypeClass::class)

        println(basicListTypeClass)
    }

    @Test
    fun shouldGenerateCustomTypeListClass() {
        data class SomeClass(val int: Int)

        data class CustomListTypeClass(val list: List<SomeClass>)

        val customListTypeClass = generate(CustomListTypeClass::class)

        println(customListTypeClass)
    }

    interface SomeInterface {}

    data class SomeClass(val int: Int) : SomeInterface

    @Test
    fun shouldGenerateAnnotationTypeListClass() {
        data class AnnotatedListTypeClass(@TestType(SomeClass::class) val list: @TestType(sdoward.com.dosser.DosserTest.SomeClass::class) List<@TestType(SomeClass::class) SomeInterface>)

        val annotatedListTypeClass = generate(AnnotatedListTypeClass::class)

        println(annotatedListTypeClass)
    }

}