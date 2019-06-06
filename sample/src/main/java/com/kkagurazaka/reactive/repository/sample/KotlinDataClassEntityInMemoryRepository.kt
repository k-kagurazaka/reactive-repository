package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository
import io.reactivex.Flowable
import io.reactivex.Observable

@InMemoryRepository(KotlinDataClassEntity::class)
interface KotlinDataClassEntityInMemoryRepository {

    fun get(): KotlinDataClassEntity?

    fun observe(): Observable<KotlinDataClassEntity>

    fun observeWithBackpressure(): Flowable<KotlinDataClassEntity>

    fun store(entity: KotlinDataClassEntity)
}
