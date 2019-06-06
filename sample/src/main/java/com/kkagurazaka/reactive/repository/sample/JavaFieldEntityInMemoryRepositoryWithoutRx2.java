package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;

@InMemoryRepository(JavaFieldEntity.class)
public interface JavaFieldEntityInMemoryRepositoryWithoutRx2 {

    @Nullable
    JavaFieldEntity get();

    void store(@Nullable JavaFieldEntity entity);
}
