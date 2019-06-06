package com.kkagurazaka.reactive.repository.processor.writer.prefs

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.definition.MethodDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsEntityDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsRepositoryDefinition
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.kkagurazaka.reactive.repository.processor.writer.RepositoryWriter
import com.kkagurazaka.reactive.repository.processor.writer.Rx2FieldSpecsBuilder
import com.kkagurazaka.reactive.repository.processor.writer.Rx2MethodSpecBuilder
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class PrefsRepositoryWriter(context: ProcessingContext, definition: PrefsRepositoryDefinition) :
    RepositoryWriter<PrefsRepositoryDefinition>(context, definition) {

    override fun TypeSpec.Builder.setup(): TypeSpec.Builder {
        val entityDefinition = definition.entityDefinition
        val entityClassName = entityDefinition.className
        val hasRx2Methods = definition.hasRx2Methods

        val preferences = FieldSpec.builder(Types.sharedPreferences, "preferences", Modifier.PRIVATE, Modifier.FINAL)
            .build()

        val defaultValue = FieldSpec.builder(entityClassName, "defaultValue", Modifier.PRIVATE, Modifier.FINAL)
            .initializer("new \$T()", entityClassName)
            .build()

        addField(preferences)
        addField(defaultValue)
        addMethod(buildConstructorMethodSpec(preferences, entityDefinition.preferencesType))
        addMethods(definition.methodDefinitions.mapNotNull {
            PrefsNonReactiveMethodSpecBuilder.build(it, hasRx2Methods)
        })

        val getterDefinition = definition.methodDefinitions.firstOrNull {
            it.type is MethodDefinition.Type.NonNullGetter || it.type is MethodDefinition.Type.PlatFormTypeGetter
        }

        if (hasRx2Methods) {
            val prepareStatement = CodeBlock.builder().addStatement("initProcessor()").build()
            addFields(Rx2FieldSpecsBuilder.build(entityDefinition))
            addMethods(definition.methodDefinitions.mapNotNull { Rx2MethodSpecBuilder.build(it, prepareStatement) })
            addMethod(buildRx2ProcessorInitializeMethodSpec(getterDefinition))
        }
        if (getterDefinition == null) {
            addMethod(PrefsNonReactiveMethodSpecBuilder.buildPrivateGetter("get", entityDefinition))
        }

        return this
    }

    override fun verify() {
        if (definition.has<MethodDefinition.Type.NullableGetter>()) {
            throw ProcessingException(
                "@PrefsRepository does not accept @Nullable getter",
                definition.element
            )
        }
    }

    private fun buildConstructorMethodSpec(
        preferences: FieldSpec,
        preferencesType: PrefsEntityDefinition.PreferencesType
    ): MethodSpec =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Types.androidContext, "context")
            .addCode(
                CodeBlock.builder()
                    .apply {
                        when (preferencesType) {
                            is PrefsEntityDefinition.PreferencesType.Default -> {
                                addStatement(
                                    "\$N = \$T.getDefaultSharedPreferences(context.getApplicationContext())",
                                    preferences,
                                    Types.preferenceManager
                                )
                            }
                            is PrefsEntityDefinition.PreferencesType.Named -> {
                                addStatement(
                                    "\$N = context.getApplicationContext().getSharedPreferences(\$S, \$T.MODE_PRIVATE)",
                                    preferences,
                                    preferencesType.name,
                                    Types.androidContext
                                )
                            }
                        }
                    }
                    .build()
            )
            .build()

    private fun buildRx2ProcessorInitializeMethodSpec(getterDefinition: MethodDefinition<*>?): MethodSpec =
        MethodSpec.methodBuilder("initProcessor")
            .addModifiers(Modifier.PRIVATE, Modifier.SYNCHRONIZED)
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("if (processor != null)")
                    .addStatement("return")
                    .endControlFlow()
                    .apply {
                        val createDefaultCode = if (getterDefinition != null) {
                            CodeBlock.builder().add("\$L()", getterDefinition.methodName).build()
                        } else {
                            CodeBlock.builder().add("get()").build()
                        }
                        Rx2FieldSpecsBuilder.buildInitializeStatement(createDefaultCode)
                            .forEach { add(it) }
                    }
                    .build()
            )
            .build()
}
