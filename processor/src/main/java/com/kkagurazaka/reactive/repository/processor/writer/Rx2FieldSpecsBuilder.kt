package com.kkagurazaka.reactive.repository.processor.writer

import com.kkagurazaka.reactive.repository.processor.definition.EntityDefinition
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterizedTypeName
import javax.lang.model.element.Modifier

object Rx2FieldSpecsBuilder {

    fun build(entityDefinition: EntityDefinition<*>): List<FieldSpec> {
        val processor = FieldSpec
            .builder(
                ParameterizedTypeName.get(Types.rx2BehaviorProcessor, entityDefinition.className),
                "processor",
                Modifier.PRIVATE
            )
            .build()

        val field = FieldSpec
            .builder(
                ParameterizedTypeName.get(Types.rx2FlowableProcessor, entityDefinition.className),
                "serialized",
                Modifier.PRIVATE
            )
            .build()

        return listOf(processor, field)
    }

    fun buildInitializeStatement(createDefaultCode: CodeBlock?): List<CodeBlock> {
        val processor = CodeBlock.builder()
            .apply {
                if (createDefaultCode != null) {
                    addStatement(
                        CodeBlock.builder()
                            .add("processor = BehaviorProcessor.createDefault(")
                            .add(createDefaultCode)
                            .add(")")
                            .build()
                    )
                } else {
                    addStatement("processor = BehaviorProcessor.create()")
                }
            }
            .build()

        val field = CodeBlock.builder()
            .addStatement("serialized = processor.toSerialized()")
            .build()

        return listOf(processor, field)
    }
}
