package com.kkagurazaka.reactive.repository.sample

import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsKey

@PrefsEntity(commitOnSave = true)
data class CommitOnSavePrefsEntity(
    @get:PrefsKey val name: String = ""
)
