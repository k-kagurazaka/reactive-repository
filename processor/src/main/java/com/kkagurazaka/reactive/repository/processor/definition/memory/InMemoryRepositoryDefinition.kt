package com.kkagurazaka.reactive.repository.processor.definition.memory

import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository
import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.RepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.AnnotationHandle
import javax.lang.model.element.TypeElement

class InMemoryRepositoryDefinition(context: ProcessingContext, element: TypeElement) :
    RepositoryDefinition<InMemoryRepository, InMemoryEntityDefinition>(context, element) {

    override val annotationHandle: AnnotationHandle<InMemoryRepository> by lazy {
        AnnotationHandle.from<InMemoryRepository>(element)
            ?: throw ProcessingException("${element.qualifiedName} is not annotated with @InMemoryRepository", element)
    }

    override val entityDefinition: InMemoryEntityDefinition by lazy {
        val entityTypeName = annotationHandle.getAsTypeName("value")
        context.inMemoryEntityDefinitions[entityTypeName]
            ?: throw ProcessingException("$entityTypeName is not annotated with @InMemoryEntity", element)
    }

    init {
        init()
    }
}
