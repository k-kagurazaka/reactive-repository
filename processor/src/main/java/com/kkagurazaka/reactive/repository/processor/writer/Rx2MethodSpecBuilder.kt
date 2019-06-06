package com.kkagurazaka.reactive.repository.processor.writer

import com.kkagurazaka.reactive.repository.processor.definition.MethodDefinition
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import javax.lang.model.element.Modifier

object Rx2MethodSpecBuilder {

    fun build(definition: MethodDefinition<*>, prepareStatement: CodeBlock?): MethodSpec? {
        val builder = MethodSpec.methodBuilder(definition.methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)

        return when (definition.type) {
            is MethodDefinition.Type.NullableGetter,
            is MethodDefinition.Type.NonNullGetter,
            is MethodDefinition.Type.PlatFormTypeGetter,
            is MethodDefinition.Type.NullableSetter,
            is MethodDefinition.Type.NonNullSetter,
            is MethodDefinition.Type.PlatFormTypeSetter -> {
                return null
            }
            is MethodDefinition.Type.Rx2Observable -> {
                builder.returns(ParameterizedTypeName.get(Types.rx2Observable, definition.entityDefinition.className))
                    .addCode(buildRx2ObservableCode(prepareStatement))
            }
            is MethodDefinition.Type.Rx2Flowable -> {
                builder.returns(ParameterizedTypeName.get(Types.rx2Flowable, definition.entityDefinition.className))
                    .addCode(buildRx2FlowableCode(prepareStatement))
            }
        }.build()
    }

    private fun buildRx2ObservableCode(prepareStatement: CodeBlock?): CodeBlock =
        CodeBlock.builder()
            .apply {
                prepareStatement?.let { add(it) }
            }
            .addStatement("return serialized.hide().toObservable()")
            .build()

    private fun buildRx2FlowableCode(prepareStatement: CodeBlock?): CodeBlock =
        CodeBlock.builder()
            .apply {
                prepareStatement?.let { add(it) }
            }
            .addStatement("return serialized.hide().onBackpressureLatest()")
            .build()
}
