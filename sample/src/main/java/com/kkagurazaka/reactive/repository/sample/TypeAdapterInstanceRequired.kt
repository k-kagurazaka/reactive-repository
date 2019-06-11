package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsKey
import com.kkagurazaka.reactive.repository.annotation.PrefsRepository
import io.reactivex.Flowable
import io.reactivex.Observable

@PrefsEntity(typeAdapter = SomeClassListInstanceRequiredTypeAdapter::class)
data class TypeAdapterInstanceRequiredEntity(
    @get:PrefsKey val isVeteran: Boolean = false,
    @get:PrefsKey val someStr: String? = null,
    @get:PrefsKey val age: Int = -1,
    @get:PrefsKey val pie: Float = 3.1415f,
    @get:PrefsKey val amount: Long = 123456789L,
    @get:PrefsKey val strList: Set<String> = emptySet(),
    @get:PrefsKey val someClassList: List<SomeClass> = listOf(SomeClass("initial"))
)

@PrefsRepository(TypeAdapterInstanceRequiredEntity::class)
interface TypeAdapterInstanceRequiredRepository {

    fun get(): TypeAdapterInstanceRequiredEntity

    fun observe(): Observable<TypeAdapterInstanceRequiredEntity>

    fun observeWithBackpressure(): Flowable<TypeAdapterInstanceRequiredEntity>

    fun store(entity: TypeAdapterInstanceRequiredEntity?)
}
