package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.PrefsTypeAdapter

data class SomeClass(val name: String)

@PrefsTypeAdapter
object SomeClassListTypeAdapter {

    @JvmStatic
    fun convert(value: List<SomeClass>): String = value.joinToString(",") { it.name }

    @JvmStatic
    fun convert(value: String): List<SomeClass> = value.split(",").map { SomeClass(it) }
}
