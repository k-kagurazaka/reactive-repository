package com.kkagurazaka.reactive.repository.processor.definition.prefs

import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.EntityDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.*
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

class PrefsEntityDefinition(
    private val context: ProcessingContext,
    element: TypeElement
) : EntityDefinition<PrefsEntity>(context, element) {

    val preferencesType: PreferencesType

    val accessorType: AccessorType

    val commitOnSave: Boolean

    override val annotationHandle: AnnotationHandle<PrefsEntity>

    init {
        init()

        annotationHandle = AnnotationHandle.from(element)
            ?: throw ProcessingException("${element.qualifiedName} is not annotated with @PrefsEntity", element)

        if (!hasEmptyConstructor) {
            throw ProcessingException("@PrefsEntity requires a constructor with no parameters", element)
        }

        val useDefaultPreferences = annotationHandle.getOrDefault<Boolean>("useDefaultPreferences")
        val preferencesName = annotationHandle.getOrDefault<String>("preferencesName")
            .takeIf { it.isNotBlank() }

        if (useDefaultPreferences && preferencesName != null) {
            throw ProcessingException(
                "preferenceName is set but useDefaultPreferences = true",
                element
            )
        }

        preferencesType = if (useDefaultPreferences) {
            PreferencesType.Default
        } else {
            val snakeName = element.simpleName.toString().toLowerSnake()
            PreferencesType.Named(preferencesName ?: snakeName)
        }

        commitOnSave = annotationHandle.getOrDefault("commitOnSave")

        val typeAdapter = annotationHandle.get<TypeMirror>("typeAdapter")
            ?.let { TypeName.get(it) }
            ?.takeUnless { it == Types.defaultTypeAdapter }
            ?.let { context.typeAdapterDefinitions[it] }

        // accessor
        val fields = mutableListOf<FieldDefinition>()
        val getters = mutableListOf<GetterDefinition>()
        val setters = mutableListOf<SetterDefinition>()

        element.enclosedElements
            .asSequence()
            .filter { it.isPrefsKeyAnnotated }
            .forEach {
                when {
                    it.isPublicNonFinalField -> {
                        fields.add(FieldDefinition(context, it, typeAdapter))
                    }
                    it.isGetter -> {
                        getters.add(GetterDefinition(context, it, typeAdapter))
                    }
                    it.isSetter -> {
                        setters.add(SetterDefinition(context, it, typeAdapter))
                    }
                }
            }

        if (fields.isNotEmpty() && (getters.isNotEmpty() || setters.isNotEmpty())) {
            throw ProcessingException("Cannot annotate field and getter / setter with @PrefsKey together", element)
        }

        accessorType = when {
            fields.isNotEmpty() -> {
                AccessorType.Fields(fields)
            }
            getters.isNotEmpty() && setters.isEmpty() -> {
                element.getSetterConstructor(getters)?.let {
                    AccessorType.GettersAndSetterConstructor(getters, it)
                } ?: run {
                    throw ProcessingException("No setters annotated with @PrefsKey found in @PrefsEntity", element)
                }
            }
            getters.isNotEmpty() && setters.isNotEmpty() -> {
                val sortedSetters = mutableListOf<SetterDefinition>()
                getters.forEach { getterDef ->
                    val setter = setters.firstOrNull { setterDef ->
                        setterDef.key == getterDef.key && setterDef.type == getterDef.type
                    } ?: return@forEach
                    sortedSetters.add(setter)
                }

                if (getters.size == sortedSetters.size) {
                    AccessorType.GettersAndSetters(getters, sortedSetters)
                } else {
                    throw ProcessingException("Getters and setters do not match", element)
                }
            }
            else -> throw ProcessingException(
                "Public non-final field or getter / setter annotated with @PrefsKey is not found",
                element
            )
        }
    }

    sealed class PreferencesType {

        object Default : PreferencesType()

        data class Named(val name: String) : PreferencesType()
    }

    sealed class AccessorType {

        data class Fields(val fields: List<FieldDefinition>) : AccessorType()

        data class GettersAndSetterConstructor(
            val getters: List<GetterDefinition>,
            val setterConstructor: ExecutableElement
        ) : AccessorType()

        data class GettersAndSetters(
            val getters: List<GetterDefinition>,
            val setters: List<SetterDefinition>
        ) : AccessorType()
    }
}
