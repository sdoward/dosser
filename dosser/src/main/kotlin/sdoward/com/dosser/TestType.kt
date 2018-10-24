package sdoward.com.dosser

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.VALUE_PARAMETER)
annotation class TestType(val kclass: KClass<*>)