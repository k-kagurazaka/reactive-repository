package com.kkagurazaka.reactive.repository.processor.definition.prefs

import com.kkagurazaka.reactive.repository.annotation.PrefsRepository
import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.RepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.AnnotationHandle
import javax.lang.model.element.TypeElement

class PrefsRepositoryDefinition(context: ProcessingContext, element: TypeElement) :
    RepositoryDefinition<PrefsRepository, PrefsEntityDefinition>(context, element) {

    override val annotationHandle: AnnotationHandle<PrefsRepository> by lazy {
        AnnotationHandle.from<PrefsRepository>(element)
            ?: throw ProcessingException("${element.qualifiedName} is not annotated with @PrefsRepository", element)
    }

    override val entityDefinition: PrefsEntityDefinition by lazy {
        val entityTypeName = annotationHandle.getAsTypeName("value")
        context.prefsEntityDefinitions[entityTypeName]
            ?: throw ProcessingException("$entityTypeName is not annotated with @PrefsEntity", element)
    }

    init {
        init()
    }
}
