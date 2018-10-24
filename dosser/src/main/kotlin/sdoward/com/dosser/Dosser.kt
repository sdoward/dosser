package sdoward.com.dosser

import java.util.Random
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

fun <T : Any> generate(clazz: KClass<T>): T {
    if (clazz.isData.not()) {
        throw DataGeneratorException("${clazz.simpleName} is not a data class")
    }
    fun generateNumber() = Random().nextInt()
    fun generateString(parameter: KParameter) = parameter.name

    val parameters = clazz.primaryConstructor!!
            .valueParameters
            .asSequence()
            .map { parameter ->
                val kclass = parameter.type.classifier as KClass<*>
                when {
                    kclass.isSubclassOf(List::class) -> {
                        if (parameter.type.arguments.first().type!!.isSubtypeOf(Number::class.defaultType)) {
                            (1..5).map { generate(parameter.type.arguments.first().type!!.jvmErasure) }
                        } else if (parameter.type.arguments.first().type!!.isSubtypeOf(String::class.defaultType)) {
                            (1..5).map { "${parameter.name} $it" }
                        } else if (parameter.hasTestTypeAnnotation()) {
                            (1..5).map { generate(parameter.getKlassFromAnntotion()) }
                        } else {
                            throw DataGeneratorException("Unable to create list an known type. Please define the test type with @TestType annoation")
                        }
                    }
                    kclass.isSubclassOf(Set::class) -> setOf("${parameter.name} item 1", "${parameter.name} item 2")
                    kclass.isSubclassOf(Map::class) -> mapOf(Pair("${parameter.name} key 1", "${parameter.name} val 1"))
                    kclass.isSubclassOf(Number::class) -> generateNumber()
                    kclass.isSubclassOf(String::class) -> generateString(parameter)
                    else -> generate(parameter.type.jvmErasure)
                }
            }.toList().toTypedArray()

    return clazz.constructors.first().call(*parameters)
}

fun KParameter.getGenericType() = type.arguments.first().type!!.jvmErasure

fun KParameter.hasTestTypeAnnotation() = annotations.find { it is TestType } != null

fun KParameter.getKlassFromAnntotion() = (annotations.find { it is TestType } as TestType).kclass

class DataGeneratorException(message: String) : Throwable(message)