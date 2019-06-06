package com.kkagurazaka.reactive.repository.processor.writer

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

abstract class Writer(protected val context: ProcessingContext) {

    abstract val packageName: String

    abstract fun buildTypeSpec(): TypeSpec

    fun buildJavaFile(): JavaFile =
        JavaFile.builder(packageName, buildTypeSpec())
            .skipJavaLangImports(true)
            .build()
}
