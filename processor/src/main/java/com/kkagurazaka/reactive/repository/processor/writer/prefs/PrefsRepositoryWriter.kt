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

    private companion object {
        val PRIVATE_FINAL = arrayOf(Modifier.PRIVATE, Modifier.FINAL)
    }

    override fun TypeSpec.Builder.setup(): TypeSpec.Builder {
        val entityDefinition = definition.entityDefinition
        val entityClassName = entityDefinition.className
        val hasRx2Methods = definition.hasRx2Methods

        // private final Context context;
        val applicationContext = FieldSpec
            .builder(Types.androidContext, "context", *PRIVATE_FINAL)
            .build()
        addField(applicationContext)

        // private final AtomicReference<SharedPreferences> preferences = new AtomicReference<>();
        val atomicPreferencesClassName = Types.atomicReference(Types.sharedPreferences)
        val preferences = FieldSpec
            .builder(atomicPreferencesClassName, "preferences", *PRIVATE_FINAL)
            .initializer("new \$T()", atomicPreferencesClassName)
            .build()
        addField(preferences)

        // private final EntityClass defaultValue = new EntityClass();
        val defaultValue = FieldSpec
            .builder(entityClassName, "defaultValue", *PRIVATE_FINAL)
            .initializer("new \$T()", entityClassName)
            .build()
        addField(defaultValue)

        // private final TypeAdapter typeAdapter;
        entityDefinition.typeAdapter?.takeIf { it.isInstanceRequired }?.let { def ->
            val typeAdapter = FieldSpec
                .builder(def.className, "typeAdapter", *PRIVATE_FINAL)
                .build()
            addField(typeAdapter)
        }

        addMethod(buildConstructorMethodSpec(entityDefinition))

        addMethods(definition.methodDefinitions.mapNotNull {
            PrefsNonReactiveMethodSpecBuilder.build(it, hasRx2Methods)
        })

        val getterDefinition = definition.methodDefinitions.firstOrNull {
            it.type is MethodDefinition.Type.NonNullGetter || it.type is MethodDefinition.Type.PlatFormTypeGetter
        }

        if (hasRx2Methods) {
            val processorPrepareStatement = CodeBlock.builder()
                .addStatement("initProcessor()")
                .build()
            addFields(Rx2FieldSpecsBuilder.build(entityDefinition))
            addMethods(definition.methodDefinitions.mapNotNull {
                Rx2MethodSpecBuilder.build(it, processorPrepareStatement)
            })
            addMethod(buildRx2ProcessorInitializeMethodSpec(getterDefinition))
        }
        if (getterDefinition == null) {
            val method = PrefsNonReactiveMethodSpecBuilder.buildPrivateGetter(
                "get",
                entityDefinition
            )
            addMethod(method)
        }

        addMethod(buildGetPreferencesMethodSpec(entityDefinition))

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

    private fun buildConstructorMethodSpec(entityDefinition: PrefsEntityDefinition): MethodSpec =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Types.androidContext, "context")
            .apply {
                entityDefinition.typeAdapter?.takeIf { it.isInstanceRequired }?.let {
                    addParameter(it.className, "typeAdapter")
                }
            }
            .addCode(
                CodeBlock.builder()
                    .addStatement("this.context = context.getApplicationContext()")
                    .apply {
                        entityDefinition.typeAdapter?.takeIf { it.isInstanceRequired }?.let {
                            addStatement("this.typeAdapter = typeAdapter")
                        }
                    }
                    .build()
            )
            .build()

    private fun buildGetPreferencesMethodSpec(entityDefinition: PrefsEntityDefinition): MethodSpec =
        MethodSpec.methodBuilder("getPreferences")
            .addModifiers(Modifier.PRIVATE)
            .returns(Types.sharedPreferences)
            .addCode(
                CodeBlock.builder()
                    .addStatement("\$T result = preferences.get()", Types.sharedPreferences)
                    .beginControlFlow("if (result == null)")
                    .apply {
                        when (val preferencesType = entityDefinition.preferencesType) {
                            is PrefsEntityDefinition.PreferencesType.Default -> {
                                addStatement(
                                    "result = \$T.getDefaultSharedPreferences(context)",
                                    Types.preferenceManager
                                )
                            }
                            is PrefsEntityDefinition.PreferencesType.Named -> {
                                addStatement(
                                    "result = context.getSharedPreferences(\$S, \$T.MODE_PRIVATE)",
                                    preferencesType.name,
                                    Types.androidContext
                                )
                            }
                        }
                        beginControlFlow("if(!preferences.compareAndSet(null, result))")
                            .addStatement("return preferences.get()")
                            .endControlFlow()
                    }
                    .endControlFlow()
                    .addStatement("return result")
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
