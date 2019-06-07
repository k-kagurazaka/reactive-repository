package com.kkagurazaka.reactive.repository.processor.writer.prefs

import com.kkagurazaka.reactive.repository.processor.definition.prefs.GetterDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.KeyDefinition
import com.kkagurazaka.reactive.repository.processor.definition.prefs.PrefsEntityDefinition
import com.squareup.javapoet.CodeBlock

object PrefsEntityStatementBuilder {

    fun buildGetStatement(entityDefinition: PrefsEntityDefinition): CodeBlock =
        CodeBlock.builder()
            .apply {
                when (val accessorType = entityDefinition.accessorType) {
                    is PrefsEntityDefinition.AccessorType.Fields -> {
                        addStatement("\$T value = new \$T()", entityDefinition.className, entityDefinition.className)
                        accessorType.fields.forEach { def ->
                            addStatement(
                                "value.\$L = preferences.\$L(\$S, defaultValue.\$L)",
                                def.name,
                                def.type.toGetMethod(),
                                def.key,
                                def.name
                            )
                        }
                        addStatement("return value")
                    }
                    is PrefsEntityDefinition.AccessorType.GettersAndSetterConstructor -> {
                        addStatement(
                            CodeBlock.builder()
                                .apply {
                                    add("return new \$T(", entityDefinition.className).indent()
                                    accessorType.getters.forEachIndexed { i, def ->
                                        val comma = if (i == accessorType.getters.lastIndex) "" else ","
                                        add(
                                            "\npreferences.\$L(\$S, defaultValue.\$L())\$L",
                                            def.type.toGetMethod(),
                                            def.key,
                                            def.name,
                                            comma
                                        )
                                    }
                                    unindent().add("\n)")
                                        .build()
                                }
                                .build()
                        )
                    }
                    is PrefsEntityDefinition.AccessorType.GettersAndSetters -> {
                        addStatement("\$T value = new \$T()", entityDefinition.className, entityDefinition.className)
                        accessorType.getters.zip(accessorType.setters).forEach { (getterDef, setterDef) ->
                            addStatement(
                                "\nvalue.\$L(preferences.\$L(\$S, defaultValue.\$L))",
                                setterDef.name,
                                getterDef.type.toGetMethod(),
                                getterDef.key,
                                getterDef.name
                            )
                        }
                        addStatement("return value")
                    }
                }
            }
            .build()

    fun buildStoreStatement(
        parameterName: String,
        accessorType: PrefsEntityDefinition.AccessorType,
        commitOnSave: Boolean
    ): CodeBlock =
        CodeBlock.builder()
            .addStatement(
                CodeBlock.builder()
                    .add("preferences.edit()")
                    .apply {
                        when (accessorType) {
                            is PrefsEntityDefinition.AccessorType.Fields -> {
                                accessorType.fields
                            }
                            is PrefsEntityDefinition.AccessorType.GettersAndSetterConstructor -> {
                                accessorType.getters
                            }
                            is PrefsEntityDefinition.AccessorType.GettersAndSetters -> {
                                accessorType.getters
                            }
                        }.forEach { def ->
                            val invoke = if (def is GetterDefinition) "()" else ""
                            add(
                                "\n.\$L(\$S, \$L.\$L\$L)",
                                def.type.toPutMethod(),
                                def.key,
                                parameterName,
                                def.name,
                                invoke
                            )
                        }

                        if (commitOnSave) {
                            add("\n.commit()")
                        } else {
                            add("\n.apply()")
                        }
                    }
                    .build()
            )
            .build()

    fun buildClearStatement(accessorType: PrefsEntityDefinition.AccessorType, commitOnSave: Boolean): CodeBlock =
        CodeBlock.builder()
            .addStatement(
                CodeBlock.builder()
                    .add("preferences.edit()")
                    .apply {
                        when (accessorType) {
                            is PrefsEntityDefinition.AccessorType.Fields -> {
                                accessorType.fields.map { it.key }
                            }
                            is PrefsEntityDefinition.AccessorType.GettersAndSetterConstructor -> {
                                accessorType.getters.map { it.key }
                            }
                            is PrefsEntityDefinition.AccessorType.GettersAndSetters -> {
                                accessorType.getters.map { it.key }
                            }
                        }.forEach { key ->
                            add("\n.remove(\$S)", key)
                        }

                        if (commitOnSave) {
                            add("\n.commit()")
                        } else {
                            add("\n.apply()")
                        }
                    }
                    .build()
            )
            .build()

    private fun KeyDefinition.Type.toGetMethod(): String =
        when (this) {
            KeyDefinition.Type.BOOLEAN -> "getBoolean"
            KeyDefinition.Type.STRING -> "getString"
            KeyDefinition.Type.INT -> "getInt"
            KeyDefinition.Type.FLOAT -> "getFloat"
            KeyDefinition.Type.LONG -> "getLong"
            KeyDefinition.Type.STRING_SET -> "getStringSet"
        }

    private fun KeyDefinition.Type.toPutMethod(): String =
        when (this) {
            KeyDefinition.Type.BOOLEAN -> "putBoolean"
            KeyDefinition.Type.STRING -> "putString"
            KeyDefinition.Type.INT -> "putInt"
            KeyDefinition.Type.FLOAT -> "putFloat"
            KeyDefinition.Type.LONG -> "putLong"
            KeyDefinition.Type.STRING_SET -> "putStringSet"
        }
}
