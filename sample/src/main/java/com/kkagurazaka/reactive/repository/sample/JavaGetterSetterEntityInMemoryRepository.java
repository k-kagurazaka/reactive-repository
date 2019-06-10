package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.InMemoryRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@InMemoryRepository(JavaGetterSetterEntity.class)
public interface JavaGetterSetterEntityInMemoryRepository {

    @Nullable
    JavaGetterSetterEntity get();

    @NonNull
    Observable<JavaGetterSetterEntity> observe();

    @NonNull
    Flowable<JavaGetterSetterEntity> observeWithBackpressure();

    void store(@NonNull JavaGetterSetterEntity entity);
}
