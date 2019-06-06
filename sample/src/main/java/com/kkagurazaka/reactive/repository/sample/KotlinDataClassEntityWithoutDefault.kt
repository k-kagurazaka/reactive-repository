package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.InMemoryEntity

@InMemoryEntity
data class KotlinDataClassEntityWithoutDefault(
    val isVeteran: Boolean,
    val someStr: String,
    val age: Int,
    val pie: Float,
    val amount: Long,
    val strList: Set<String>
)
