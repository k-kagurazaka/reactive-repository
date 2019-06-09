package com.kkagurazaka.reactive.repository.processor.tools

import com.google.common.base.CaseFormat
import com.kkagurazaka.reactive.repository.processor.definition.prefs.GetterDefinition
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.*
import javax.lang.model.type.TypeKind

fun String.lowerCamelToUpperCamel(): String =
    CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, this)

fun String.toLowerCamel(): String =
    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)

fun String.toLowerSnake(): String =
    CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)

val Element.isNullableAnnotated: Boolean
    get() = annotationMirrors.any {
        val className = ClassName.get(it.annotationType)
        className == Types.annotationNullableJetBrains ||
                className == Types.annotationNullableAndroidX ||
                className == Types.annotationNullableSupport
    }

val Element.isNonNullAnnotated: Boolean
    get() = annotationMirrors.any {
        val className = ClassName.get(it.annotationType)
        className == Types.annotationNonNullJetBrains ||
                className == Types.annotationNonNullAndroidX ||
                className == Types.annotationNonNullSupport
    }
val Element.isPrefsKeyAnnotated: Boolean
    get() = annotationMirrors.any {
        val className = ClassName.get(it.annotationType)
        className == Types.prefsKey
    }

val Element.isPublicNonFinalField: Boolean
    get() = kind == ElementKind.FIELD &&
            modifiers.contains(Modifier.PUBLIC) &&
            !modifiers.contains(Modifier.FINAL)

val Element.isPublicStaticMethod: Boolean
    get() = kind == ElementKind.METHOD &&
            modifiers.contains(Modifier.PUBLIC) &&
            modifiers.contains(Modifier.STATIC)

val Element.isGetter: Boolean
    get() {
        if (kind != ElementKind.METHOD) {
            return false
        }

        val executable = this as ExecutableElement
        if (executable.parameters.isNotEmpty() || executable.returnType.kind == TypeKind.VOID) {
            return false
        }

        val name = simpleName.toString()
        return name.startsWith("get") || name.startsWith("is")
    }

val Element.isSetter: Boolean
    get() {
        if (kind != ElementKind.METHOD) {
            return false
        }

        val executable = this as ExecutableElement
        if (executable.parameters.isEmpty() || executable.returnType.kind != TypeKind.VOID) {
            return false
        }

        val name = simpleName.toString()
        return name.startsWith("set")
    }

val TypeElement.hasEmptyConstructor: Boolean
    get() = enclosedElements.any {
        it.kind == ElementKind.CONSTRUCTOR &&
                (it as ExecutableElement).parameters.isEmpty()
    }

fun TypeElement.getSetterConstructor(getters: List<GetterDefinition>): ExecutableElement? =
    enclosedElements.asSequence()
        .filter { it.kind == ElementKind.CONSTRUCTOR }
        .map { it as ExecutableElement }
        .filter { it.parameters.size == getters.size }
        .firstOrNull()

fun <T : Any> TypeElement.mapIfMethod(block: (method: ExecutableElement) -> T): List<T> =
    enclosedElements.asSequence()
        .filter { it.kind == ElementKind.METHOD }
        .map { it as ExecutableElement }
        .map(block)
        .toList()

val TypeName.isSupportedByPrefs: Boolean
    get() = this == TypeName.BOOLEAN ||
            this == Types.string ||
            this == TypeName.INT ||
            this == TypeName.FLOAT ||
            this == TypeName.LONG ||
            this == Types.stringSet
