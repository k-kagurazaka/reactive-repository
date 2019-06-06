package com.kkagurazaka.reactive.repository.processor.tools

import com.squareup.javapoet.TypeName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

class AnnotationHandle<T : Annotation>(
    private val element: Element,
    val annotation: KClass<T>,
    val mirror: AnnotationMirror
) {

    inline fun <reified T> get(name: String): T? =
        mirror.elementValues.entries.asSequence()
            .filter { entry -> entry.key.simpleName.contentEquals(name) }
            .map { entry -> T::class.java.cast(entry.value.value) }
            .firstOrNull()

    inline fun <reified T> getOrDefault(name: String): T =
        get<T>(name) ?: T::class.java.cast(annotation.java.getMethod(name).defaultValue)

    fun getAsTypeName(name: String): TypeName = TypeName.get(get<TypeMirror>(name))

    companion object {

        inline fun <reified T : Annotation> from(element: Element): AnnotationHandle<T>? =
            element.annotationMirrors
                .asSequence()
                .filter { mirror ->
                    val typeElement = mirror.annotationType.asElement() as TypeElement
                    typeElement.qualifiedName.contentEquals(T::class.qualifiedName)
                }
                .map { mirror -> AnnotationHandle(element, T::class, mirror) }
                .firstOrNull()
    }
}
