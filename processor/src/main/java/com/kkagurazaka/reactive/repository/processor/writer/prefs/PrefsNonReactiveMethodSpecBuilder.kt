package com.kkagurazaka.reactive.repository.processor.writer.prefs

import com.kkagurazaka.reactive.repository.processor.definition.MethodDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsEntityDefinition
import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.Modifier

object PrefsNonReactiveMethodSpecBuilder {

    fun build(definition: MethodDefinition<PrefsEntityDefinition>, hasRx2Processor: Boolean): MethodSpec? {
        val entityDefinition = definition.entityDefinition

        val builder = MethodSpec.methodBuilder(definition.methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)

        return when (val type = definition.type) {
            is MethodDefinition.Type.Rx2Observable, is MethodDefinition.Type.Rx2Flowable -> {
                return null
            }
            is MethodDefinition.Type.NullableGetter,
            is MethodDefinition.Type.NonNullGetter,
            is MethodDefinition.Type.PlatFormTypeGetter -> {
                builder.returns(entityDefinition.className)
                    .addGetPreferenceCode(entityDefinition)
            }
            is MethodDefinition.Type.NullableSetter -> {
                builder.setupAsNullableSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
            is MethodDefinition.Type.NonNullSetter -> {
                builder.setupAsNonNullSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
            is MethodDefinition.Type.PlatFormTypeSetter -> {
                builder.setupAsNullableSetter(entityDefinition, type.parameterName, hasRx2Processor)
            }
        }.build()
    }

    fun buildPrivateGetter(name: String, entityDefinition: PrefsEntityDefinition): MethodSpec? =
        MethodSpec.methodBuilder(name)
            .addModifiers(Modifier.PRIVATE)
            .returns(entityDefinition.className)
            .addGetPreferenceCode(entityDefinition)
            .build()

    private fun MethodSpec.Builder.setupAsNullableSetter(
        entityDefinition: PrefsEntityDefinition,
        parameterName: String,
        hasRx2Processor: Boolean
    ): MethodSpec.Builder =
        addParameter(entityDefinition.className, parameterName)
            .apply {
                if (hasRx2Processor) {
                    addProcessorInitializeCode()
                }
            }
            .addClearPreferenceCode(entityDefinition, parameterName, hasRx2Processor)
            .addStoreToPreferenceCode(entityDefinition, parameterName)
            .apply {
                if (hasRx2Processor) {
                    addProcessorOnNextCode(parameterName)
                }
            }

    private fun MethodSpec.Builder.setupAsNonNullSetter(
        entityDefinition: PrefsEntityDefinition,
        parameterName: String,
        hasRx2Processor: Boolean
    ): MethodSpec.Builder =
        addParameter(entityDefinition.className, parameterName)
            .apply {
                if (hasRx2Processor) {
                    addProcessorInitializeCode()
                }
            }
            .addStoreToPreferenceCode(entityDefinition, parameterName)
            .apply {
                if (hasRx2Processor) {
                    addProcessorOnNextCode(parameterName)
                }
            }

    private fun MethodSpec.Builder.addGetPreferenceCode(entityDefinition: PrefsEntityDefinition): MethodSpec.Builder =
        addCode(PrefsEntityStatementBuilder.buildGetStatement(entityDefinition))

    private fun MethodSpec.Builder.addClearPreferenceCode(
        entityDefinition: PrefsEntityDefinition,
        parameterName: String,
        hasRx2Processor: Boolean
    ): MethodSpec.Builder =
        beginControlFlow("if (\$L == null)", parameterName)
            .addCode(PrefsEntityStatementBuilder.buildClearStatement(entityDefinition.accessorType))
            .apply {
                if (hasRx2Processor) {
                    addStatement("serialized.onNext(new \$T())", entityDefinition.className)
                }
            }
            .addStatement("return")
            .endControlFlow()

    private fun MethodSpec.Builder.addStoreToPreferenceCode(
        entityDefinition: PrefsEntityDefinition,
        parameterName: String
    ): MethodSpec.Builder =
        addCode(PrefsEntityStatementBuilder.buildStoreStatement(parameterName, entityDefinition.accessorType))

    private fun MethodSpec.Builder.addProcessorInitializeCode(): MethodSpec.Builder =
        addStatement("initProcessor()")

    private fun MethodSpec.Builder.addProcessorOnNextCode(parameterName: String): MethodSpec.Builder =
        addStatement("serialized.onNext(\$L)", parameterName)
}
