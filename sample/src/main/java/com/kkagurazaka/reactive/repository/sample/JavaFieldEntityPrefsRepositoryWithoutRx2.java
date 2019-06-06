package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;

@PrefsRepository(JavaFieldEntity.class)
public interface JavaFieldEntityPrefsRepositoryWithoutRx2 {

    @NonNull
    JavaFieldEntity get();

    void store(@Nullable JavaFieldEntity entity);
}
