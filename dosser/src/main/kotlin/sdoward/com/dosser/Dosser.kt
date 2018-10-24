package sdoward.com.dosser

import java.util.Random
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

fun <T : Any> generate(clazz: KClass<T>): T {
    fun generateNumber() = Random().nextInt()
    fun generateBoolean() = Random().nextBoolean()
    fun generateString(parameter: KParameter) = parameter.name
    val basicType = when {
        clazz.isSubclassOf(Number::class) -> generateNumber()
        clazz.isSubclassOf(String::class) -> clazz.simpleName
        clazz.isSubclassOf(Boolean::class) -> generateBoolean()
        else -> null
    }
    if (basicType != null) {
        return clazz.cast(basicType)
    }
    val parameters = clazz.primaryConstructor!!
            .valueParameters
            .asSequence()
            .map { parameter ->
                val kclass = parameter.type.classifier as KClass<*>
                when {
                    kclass.isSubclassOf(List::class) -> {
                        if (parameter.isBasicType()) {
                            (1..5).map { generate(parameter.getBasicType()) }
                        } else if (parameter.hasTestTypeAnnotation()) {
                            (1..5).map { generate(parameter.getKlassFromAnntotion()) }
                        } else if (parameter.type.arguments.first().type!!.classifier is KClass<*>) {
                            val clazz = parameter.type.arguments.first().type!!.classifier as KClass<*>
                            (1..5).map { generate(clazz) }
                        } else {
                            throw DataGeneratorException("Unable to create list an unknown type. Please define the test type with @TestType annoation")
                        }
                    }
                    kclass.isSubclassOf(Set::class) -> setOf("${parameter.name} item 1", "${parameter.name} item 2")
                    kclass.isSubclassOf(Map::class) -> mapOf(Pair("${parameter.name} key 1", "${parameter.name} val 1"))
                    kclass.isSubclassOf(Number::class) -> generateNumber()
                    kclass.isSubclassOf(String::class) -> generateString(parameter)
                    kclass.isSubclassOf(Boolean::class) -> generateBoolean()
                    else -> generate(parameter.type.jvmErasure)
                }
            }.toList().toTypedArray()

    return clazz.constructors.first().call(*parameters)
}

val basicClasses = listOf(Boolean::class, Number::class, String::class)

fun KParameter.isBasicType(): Boolean {
    val type = type.arguments.first().type!!
    return basicClasses.any { type.isSubtypeOf(it.defaultType) }
}

fun KParameter.getBasicType(): KClass<*> {
    val type = type.arguments.first().type!!
    return basicClasses.first { type.isSubtypeOf(it.defaultType) }
}

fun KParameter.getGenericType() = type.arguments.first().type!!.jvmErasure

fun KParameter.hasTestTypeAnnotation(): Boolean {
    println(this.annotations)
    return annotations.any { it is TestType }
}

fun KParameter.getKlassFromAnntotion() = (annotations.find { it is TestType } as TestType).kclass

class DataGeneratorException(message: String) : Throwable(message)