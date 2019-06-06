package com.kkagurazaka.reactive.repository.processor.writer

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.EntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.RepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

abstract class RepositoryWriter<RD : RepositoryDefinition<out Annotation, out EntityDefinition<out Annotation>>>(
    context: ProcessingContext,
    protected val definition: RD
) : Writer(context) {

    final override val packageName: String = definition.packageName

    final override fun buildTypeSpec(): TypeSpec {
        verify()

        return TypeSpec.classBuilder(definition.generatedClassName)
            .addSuperinterface(definition.baseClassName)
            .setup()
            .build()
    }

    protected abstract fun verify()

    protected abstract fun TypeSpec.Builder.setup(): TypeSpec.Builder

    private fun buildProcessorFieldSpec(entityClassName: ClassName, createDefault: Boolean): FieldSpec =
        FieldSpec
            .builder(
                ParameterizedTypeName.get(Types.rx2BehaviorProcessor, entityClassName),
                "processor",
                Modifier.PRIVATE
            )
            .apply {
                if (createDefault) {
                    initializer("BehaviorProcessor.createDefault(new \$T())", entityClassName)
                } else {
                    initializer("BehaviorProcessor.create()")
                }
            }
            .build()

    private fun buildSerializedFieldSpec(entityClassName: ClassName, processor: FieldSpec): FieldSpec =
        FieldSpec
            .builder(
                ParameterizedTypeName.get(Types.rx2FlowableProcessor, entityClassName),
                "serialized",
                Modifier.PRIVATE
            )
            .initializer("\$N.toSerialized()", processor)
            .build()
}
