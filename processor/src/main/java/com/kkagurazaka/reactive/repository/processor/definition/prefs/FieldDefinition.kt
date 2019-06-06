package com.kkagurazaka.reactive.repository.processor.definition.prefs

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.tools.toLowerSnake
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element

class FieldDefinition(context: ProcessingContext, element: Element) : KeyDefinition(context, element) {

    override val key: String = specifiedKey ?: name.toLowerSnake()

    override val type: Type = Type.from(TypeName.get(element.asType()), element)
}
