package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsKey

@InMemoryEntity
@PrefsEntity(typeAdapter = SomeClassListTypeAdapter::class)
data class KotlinDataClassEntity(
    @get:PrefsKey val isVeteran: Boolean = false,
    @get:PrefsKey val someStr: String = "",
    @get:PrefsKey val age: Int = -1,
    @get:PrefsKey val pie: Float = 3.1415f,
    @get:PrefsKey val amount: Long = 123456789L,
    @get:PrefsKey val strList: Set<String> = setOf("1", "2", "3"),
    @get:PrefsKey val someClassList: List<SomeClass> = emptyList()
)
