package com.kkagurazaka.reactive.repository.processor.definition.prefs

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.isPublicMethod
import com.kkagurazaka.reactive.repository.processor.tools.isSupportedByPrefs
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

class TypeAdapterDefinition(context: ProcessingContext, val element: TypeElement) {

    val className: ClassName = ClassName.get(element)
    val adapterMethods: List<AdapterMethodPair>
    val isInstanceRequired: Boolean

    init {
        val toPrefsElements = mutableMapOf<AdapterMethodKey, AdapterMethodValue>()
        val toTypeElements = mutableMapOf<AdapterMethodKey, AdapterMethodValue>()

        element.enclosedElements
            .asSequence()
            .filter { it.isPublicMethod }
            .map { it as ExecutableElement }
            .forEach { method ->
                if (method.parameters.size != 1 || method.returnType.kind == TypeKind.VOID) {
                    return@forEach
                }

                val isInstanceRequired = !method.modifiers.contains(Modifier.STATIC)
                val parameterType = TypeName.get(method.parameters.single().asType())
                val returnType = TypeName.get(method.returnType)

                when {
                    parameterType.isSupportedByPrefs && returnType.isSupportedByPrefs -> {
                        throw ProcessingException(
                            "Both type of parameter ($parameterType) and return value ($returnType) is already supported by SharedPreferences",
                            method
                        )
                    }
                    parameterType.isSupportedByPrefs && !returnType.isSupportedByPrefs -> {
                        val adapterTypes = AdapterMethodKey(returnType, parameterType)
                        if (toTypeElements.containsKey(adapterTypes)) {
                            throw ProcessingException(
                                "TypeAdapter from $parameterType to $returnType is already registered",
                                method
                            )
                        }
                        toTypeElements[adapterTypes] = AdapterMethodValue(method, isInstanceRequired)
                    }
                    !parameterType.isSupportedByPrefs && returnType.isSupportedByPrefs -> {
                        val adapterTypes = AdapterMethodKey(parameterType, returnType)
                        if (toPrefsElements.containsKey(adapterTypes)) {
                            throw ProcessingException(
                                "TypeAdapter from $parameterType to $returnType is already registered",
                                method
                            )
                        }
                        toPrefsElements[adapterTypes] = AdapterMethodValue(method, isInstanceRequired)
                    }
                }
            }

        // verify
        toPrefsElements.keys.forEach {
            if (!toTypeElements.containsKey(it)) {
                throw ProcessingException(
                    "TypeAdapter from ${it.type} to ${it.prefsType} found but inverse one does not found",
                    element
                )
            }
        }
        toTypeElements.keys.forEach {
            if (!toPrefsElements.containsKey(it)) {
                throw ProcessingException(
                    "TypeAdapter from ${it.prefsType} to ${it.type} found but inverse one does not found",
                    element
                )
            }
        }

        adapterMethods = toPrefsElements.map { (key, toPrefsValue) ->
            val toTypeValue = toTypeElements[key]!! // no problem because already verified
            AdapterMethodPair(
                className,
                key.type,
                key.prefsType,
                toTypeValue.method,
                toPrefsValue.method
            )
        }

        isInstanceRequired = toPrefsElements.values.any { it.isInstanceRequired } ||
                toTypeElements.values.any { it.isInstanceRequired }
    }

    data class AdapterMethodPair(
        val className: ClassName,
        val type: TypeName,
        val prefsType: TypeName,
        val toTypeMethod: ExecutableElement,
        val toPrefsMethod: ExecutableElement
    )

    private data class AdapterMethodKey(
        val type: TypeName,
        val prefsType: TypeName
    )

    private data class AdapterMethodValue(
        val method: ExecutableElement,
        val isInstanceRequired: Boolean
    )
}
