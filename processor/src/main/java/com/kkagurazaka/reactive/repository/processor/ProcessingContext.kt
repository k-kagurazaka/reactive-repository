package com.kkagurazaka.reactive.repository.processor

import com.kkagurazaka.reactive.repository.processor.definition.memory.InMemoryEntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.memory.InMemoryRepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsEntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsRepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.TypeAdapterDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

class ProcessingContext(private val processingEnv: ProcessingEnvironment) {

    val typeAdapterDefinitions: Map<TypeName, TypeAdapterDefinition>
        get() = mutableTypeAdapterDefinitions
    val inMemoryEntityDefinitions: Map<ClassName, InMemoryEntityDefinition>
        get() = mutableInMemoryEntityDefinitions
    val prefsEntityDefinitions: Map<ClassName, PrefsEntityDefinition>
        get() = mutablePrefsEntityDefinitions
    val inMemoryRepositoryDefinitions: List<InMemoryRepositoryDefinition>
        get() = mutableInMemoryRepositoryDefinitions
    val prefsRepositoryDefinitions: List<PrefsRepositoryDefinition>
        get() = mutablePrefsRepositoryDefinitions

    private val mutableTypeAdapterDefinitions = mutableMapOf<TypeName, TypeAdapterDefinition>()
    private val mutableInMemoryEntityDefinitions = mutableMapOf<ClassName, InMemoryEntityDefinition>()
    private val mutablePrefsEntityDefinitions = mutableMapOf<ClassName, PrefsEntityDefinition>()
    private val mutableInMemoryRepositoryDefinitions = mutableListOf<InMemoryRepositoryDefinition>()
    private val mutablePrefsRepositoryDefinitions = mutableListOf<PrefsRepositoryDefinition>()
    private val errors = mutableListOf<ProcessingException>()

    val elements: Elements get() = processingEnv.elementUtils

    fun add(definition: TypeAdapterDefinition) {
        mutableTypeAdapterDefinitions[definition.className] = definition
    }

    fun add(definition: InMemoryEntityDefinition) {
        mutableInMemoryEntityDefinitions[definition.className] = definition
    }

    fun add(definition: PrefsEntityDefinition) {
        mutablePrefsEntityDefinitions[definition.className] = definition
    }

    fun add(definition: InMemoryRepositoryDefinition) {
        mutableInMemoryRepositoryDefinitions.add(definition)
    }

    fun add(definition: PrefsRepositoryDefinition) {
        mutablePrefsRepositoryDefinitions.add(definition)
    }

    fun note(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "[${ReactiveRepositoryProcessor.TAG}] $message")
    }

    fun warn(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "[${ReactiveRepositoryProcessor.TAG}] $message")
    }

    fun addError(message: String, element: Element?, cause: Throwable? = null) {
        errors.add(ProcessingException(message, element, cause))
    }

    fun addError(processingException: ProcessingException) {
        errors.add(processingException)
    }

    fun printErrors() {
        errors.forEach { error ->
            val sw = StringWriter()
            error.printStackTrace(PrintWriter(sw))
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, sw.toString(), error.element)
        }
    }
}
