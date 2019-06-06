package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@InMemoryRepository(JavaFieldEntity.class)
public interface JavaFieldEntityInMemoryRepository {

    @Nullable
    JavaFieldEntity get();

    @NonNull
    Observable<JavaFieldEntity> observe();

    @NonNull
    Flowable<JavaFieldEntity> observeWithBackpressure();

    void store(@NonNull JavaFieldEntity entity);
}
