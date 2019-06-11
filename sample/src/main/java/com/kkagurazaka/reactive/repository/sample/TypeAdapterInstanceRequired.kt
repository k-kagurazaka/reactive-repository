package com.kkagurazaka.reactive.repository.sample

import android.content.Context
import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsKey
import com.kkagurazaka.reactive.repository.annotation.PrefsRepository

@PrefsEntity(typeAdapter = SomeClassListInstanceRequiredTypeAdapter::class)
data class TypeAdapterInstanceRequiredEntity(
    @get:PrefsKey val someClassList: List<SomeClass> = listOf(SomeClass("initial"))
)

@PrefsRepository(TypeAdapterInstanceRequiredEntity::class)
interface TypeAdapterInstanceRequiredRepository {

    fun get(): TypeAdapterInstanceRequiredEntity

    fun save(entity: TypeAdapterInstanceRequiredEntity?)
}

fun createTypeAdapterInstanceRequiredRepository(context: Context): TypeAdapterInstanceRequiredRepository =
    TypeAdapterInstanceRequiredRepositoryImpl(context, SomeClassListInstanceRequiredTypeAdapter())
