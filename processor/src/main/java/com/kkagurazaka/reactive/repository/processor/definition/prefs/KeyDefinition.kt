package com.kkagurazaka.reactive.repository.processor.definition.prefs

import com.kkagurazaka.reactive.repository.annotation.PrefsKey
import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.AnnotationHandle
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element

abstract class KeyDefinition(context: ProcessingContext, val element: Element) {

    val name: String = element.simpleName.toString()

    abstract val key: String
    abstract val type: Type

    protected val specifiedKey: String?

    init {
        val annotationHandle = AnnotationHandle.from<PrefsKey>(element)
            ?: throw ProcessingException("${TypeName.get(element.asType())} is not annotated with @PrefsKey", element)

        specifiedKey = annotationHandle.getOrDefault<String>("value")
            .takeIf { it.isNotBlank() }
    }

    enum class Type {
        BOOLEAN,
        STRING,
        INT,
        FLOAT,
        LONG,
        STRING_SET;

        companion object {

            fun from(typeName: TypeName, element: Element): Type =
                when (typeName) {
                    TypeName.BOOLEAN -> BOOLEAN
                    Types.string -> STRING
                    TypeName.INT -> INT
                    TypeName.FLOAT -> FLOAT
                    TypeName.LONG -> LONG
                    Types.stringSet -> STRING_SET
                    else -> throw ProcessingException(
                        "${TypeName.get(element.asType())} is not supported by SharedPreferences",
                        element
                    )
                }
        }
    }
}
