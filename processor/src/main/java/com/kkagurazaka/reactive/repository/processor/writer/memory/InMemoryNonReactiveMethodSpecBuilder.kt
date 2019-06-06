package com.kkagurazaka.reactive.repository.processor.writer.memory

import com.kkagurazaka.reactive.repository.processor.definition.MethodDefinition
import com.kkagurazaka.reactive.repository.processor.definition.memory.InMemoryEntityDefinition
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.Modifier

object InMemoryNonReactiveMethodSpecBuilder {

    fun build(definition: MethodDefinition<InMemoryEntityDefinition>, hasRx2Processor: Boolean): MethodSpec? {
        val entityDefinition = definition.entityDefinition

        val builder = MethodSpec.methodBuilder(definition.methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)

        return when (val type = definition.type) {
            is MethodDefinition.Type.Rx2Observable, is MethodDefinition.Type.Rx2Flowable -> {
                return null
            }
            is MethodDefinition.Type.NullableGetter, MethodDefinition.Type.PlatFormTypeGetter -> {
                builder.returns(entityDefinition.className)
                    .apply {
                        if (hasRx2Processor) {
                            addCode(buildProcessorNullableGetterCode())
                        } else {
                            addCode(buildValueGetterCode())
                        }
                    }
            }
            is MethodDefinition.Type.NonNullGetter -> {
                builder.returns(entityDefinition.className)
                    .apply {
                        if (hasRx2Processor) {
                            addCode(buildProcessorNonNullGetterCode(entityDefinition))
                        } else {
                            addCode(buildValueGetterCode())
                        }
                    }
            }
            is MethodDefinition.Type.NullableSetter -> {
                builder.setupAsSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
            is MethodDefinition.Type.NonNullSetter -> {
                builder.setupAsSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
            is MethodDefinition.Type.PlatFormTypeSetter -> {
                builder.setupAsSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
        }.build()
    }

    private fun MethodSpec.Builder.setupAsSetter(
        entityDefinition: InMemoryEntityDefinition,
        parameterName: String,
        hasRx2Processor: Boolean
    ): MethodSpec.Builder =
        addParameter(entityDefinition.className, parameterName)
            .apply {
                if (hasRx2Processor) {
                    addCode(buildProcessorOnNextCode(parameterName))
                } else {
                    addModifiers(Modifier.SYNCHRONIZED)
                        .addCode(buildValueSetterCode(parameterName))
                }
            }

    private fun buildValueGetterCode(): CodeBlock =
        CodeBlock.builder()
            .addStatement("return value")
            .build()

    private fun buildValueSetterCode(parameterName: String): CodeBlock =
        CodeBlock.builder()
            .addStatement("value = \$L", parameterName)
            .build()

    private fun buildProcessorNullableGetterCode(): CodeBlock =
        CodeBlock.builder()
            .addStatement("return processor.getValue()")
            .build()

    private fun buildProcessorNonNullGetterCode(definition: InMemoryEntityDefinition): CodeBlock =
        CodeBlock.builder()
            .addStatement("\$T value = processor.getValue()", definition.className)
            .addStatement("return value != null ? value : new \$T()", definition.className)
            .build()

    private fun buildProcessorOnNextCode(parameterName: String): CodeBlock =
        CodeBlock.builder()
            .addStatement("serialized.onNext(\$L)", parameterName)
            .build()
}
