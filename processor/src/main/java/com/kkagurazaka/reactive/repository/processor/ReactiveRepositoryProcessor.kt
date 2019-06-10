package com.kkagurazaka.reactive.repository.processor

import com.google.auto.service.AutoService
import com.kkagurazaka.reactive.repository.annotation.*
import com.kkagurazaka.reactive.repository.processor.definition.memory.InMemoryEntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.memory.InMemoryRepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsEntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsRepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.TypeAdapterDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.writer.memory.InMemoryRepositoryWriter
import com.kkagurazaka.reactive.repository.processor.writer.prefs.PrefsRepositoryWriter
import com.squareup.javapoet.JavaFile
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ReactiveRepositoryProcessor : AbstractProcessor() {

    companion object {
        val TAG: String = ReactiveRepositoryProcessor::class.java.simpleName
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.RELEASE_8

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(
            InMemoryEntity::class.java.name,
            PrefsEntity::class.java.name,
            InMemoryRepository::class.java.name,
            PrefsRepository::class.java.name
        )

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) {
            return true
        }

        val context = ProcessingContext(processingEnv)

        context.note("processing start")

        try {
            // InMemory
            roundEnv.getElementsAnnotatedWith(InMemoryEntity::class.java)
                .asSequence()
                .map { element -> InMemoryEntityDefinition(context, element as TypeElement) }
                .forEach(context::add)

            roundEnv.getElementsAnnotatedWith(InMemoryRepository::class.java)
                .asSequence()
                .map { element -> InMemoryRepositoryDefinition(context, element as TypeElement) }
                .forEach(context::add)

            context.inMemoryRepositoryDefinitions.forEach { definition ->
                InMemoryRepositoryWriter(context, definition)
                    .buildJavaFile()
                    .writeToFiler(definition.element)
            }

            // Prefs
            roundEnv.getElementsAnnotatedWith(PrefsTypeAdapter::class.java)
                .asSequence()
                .map { element -> TypeAdapterDefinition(context, element as TypeElement) }
                .forEach(context::add)

            roundEnv.getElementsAnnotatedWith(PrefsEntity::class.java)
                .asSequence()
                .map { element -> PrefsEntityDefinition(context, element as TypeElement) }
                .forEach(context::add)

            roundEnv.getElementsAnnotatedWith(PrefsRepository::class.java)
                .asSequence()
                .map { element -> PrefsRepositoryDefinition(context, element as TypeElement) }
                .forEach(context::add)

            // verify
            verifyPrefsKey(context)

            context.prefsRepositoryDefinitions.forEach { definition ->
                PrefsRepositoryWriter(context, definition)
                    .buildJavaFile()
                    .writeToFiler(definition.element)
            }
        } catch (e: ProcessingException) {
            context.addError(e)
        } catch (e: Throwable) {
            context.addError("Unexpected exception is thrown", null, e)
        }

        context.printErrors()

        context.note("processing complete")

        return false
    }

    private fun verifyPrefsKey(context: ProcessingContext) {
        data class PrefsKey(val prefsName: String?, val key: String)

        val prefsKeys = mutableMapOf<PrefsKey, Unit>()
        context.prefsEntityDefinitions.values.forEach { definition ->
            val prefsName = when (val type = definition.preferencesType) {
                is PrefsEntityDefinition.PreferencesType.Default -> null
                is PrefsEntityDefinition.PreferencesType.Named -> type.name
            }
            when (val accessorType = definition.accessorType) {
                is PrefsEntityDefinition.AccessorType.Fields -> accessorType.fields.map { it.key }
                is PrefsEntityDefinition.AccessorType.GettersAndSetterConstructor -> accessorType.getters.map { it.key }
                is PrefsEntityDefinition.AccessorType.GettersAndSetters -> accessorType.getters.map { it.key }
            }.forEach { key ->
                val prefsKey = PrefsKey(prefsName, key)
                if (prefsKeys.containsKey(prefsKey)) {
                    throw ProcessingException(
                        "Conflict SharedPreferences key: ${prefsName ?: "DefaultSharedPreferences"}.$key",
                        element = null
                    )
                } else {
                    prefsKeys[prefsKey] = Unit
                }
            }
        }
    }

    private fun JavaFile.writeToFiler(element: Element) {
        try {
            writeTo(processingEnv.filer)
        } catch (t: Throwable) {
            throw ProcessingException("Failed to write " + typeSpec.name, element, t)
        }
    }
}
