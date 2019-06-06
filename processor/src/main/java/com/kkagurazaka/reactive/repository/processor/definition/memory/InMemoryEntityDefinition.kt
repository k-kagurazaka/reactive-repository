package com.kkagurazaka.reactive.repository.processor.definition.memory

import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity
import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.EntityDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.AnnotationHandle
import javax.lang.model.element.TypeElement

class InMemoryEntityDefinition(context: ProcessingContext, element: TypeElement) :
    EntityDefinition<InMemoryEntity>(context, element) {

    override val annotationHandle: AnnotationHandle<InMemoryEntity>

    init {
        init()

        annotationHandle = AnnotationHandle.from(element)
            ?: throw ProcessingException("${element.qualifiedName} is not annotated with @InMemoryEntity", element)

    }
}
