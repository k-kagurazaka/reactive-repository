package com.kkagurazaka.reactive.repository.processor.definition

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.AnnotationHandle
import com.kkagurazaka.reactive.repository.processor.tools.mapIfMethod
import com.squareup.javapoet.ClassName
import javax.lang.model.element.TypeElement

abstract class RepositoryDefinition<R : Annotation, ED : EntityDefinition<out Annotation>>(
    context: ProcessingContext,
    val element: TypeElement
) {

    private companion object {
        const val DEFAULT_SUFFIX = "Impl"
    }

    val packageName: String = context.elements.getPackageOf(element).toString()

    val baseClassName: ClassName = ClassName.get(element)

    val generatedClassName: ClassName by lazy {
        val baseName = element.simpleName
        val name = annotationHandle.getOrDefault<String>("generatedClassName")
            .takeIf { it.isNotBlank() }
            ?: "$baseName$DEFAULT_SUFFIX"
        ClassName.get(packageName, name)
    }

    val methodDefinitions: List<MethodDefinition<ED>> by lazy {
        element.mapIfMethod { method -> MethodDefinition(context, method, entityDefinition) }
    }

    abstract val entityDefinition: ED

    protected abstract val annotationHandle: AnnotationHandle<R>

    protected open fun init() {
        val hasGetters = listOf(
            has<MethodDefinition.Type.NullableGetter>(),
            has<MethodDefinition.Type.NonNullGetter>(),
            has<MethodDefinition.Type.PlatFormTypeGetter>()
        )
        if (hasGetters.count { it } > 1) {
            throw ProcessingException("Multiple getters found in ${element.qualifiedName}", element)
        }

        val hasSetters = listOf(
            has<MethodDefinition.Type.NullableSetter>(),
            has<MethodDefinition.Type.NonNullSetter>(),
            has<MethodDefinition.Type.PlatFormTypeSetter>()
        )
        if (hasSetters.count { it } > 1) {
            throw ProcessingException("Multiple setters found in ${element.qualifiedName}", element)
        }

        if (has<MethodDefinition.Type.NonNullGetter>() && !entityDefinition.hasEmptyConstructor) {
            throw ProcessingException(
                "@NonNull getter found in ${element.qualifiedName} but ${entityDefinition.className} does not have a constructor with no parameters",
                element
            )
        }
    }

    inline fun <reified T : MethodDefinition.Type> has(): Boolean =
        methodDefinitions.any { it.type is T }

    val hasRx2Methods: Boolean
        get() = has<MethodDefinition.Type.Rx2Flowable>() || has<MethodDefinition.Type.Rx2Observable>()
}
