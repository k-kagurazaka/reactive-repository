package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.PrefsRepository

@PrefsRepository(CommitOnSavePrefsEntity::class)
interface CommitOnSavePrefsEntityRepository {

    fun get(): CommitOnSavePrefsEntity

    fun store(entity: CommitOnSavePrefsEntity?)
}
