package sdoward.com.dosser

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Retention(RetentionPolicy.SOURCE)
@Target(AnnotationTarget.TYPE)
annotation class TestType(val kclass: KClass<*>)