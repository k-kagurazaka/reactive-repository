package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@PrefsRepository(JavaGetterSetterEntity.class)
public interface JavaGetterSetterEntityPrefsRepository {

    @NonNull
    JavaGetterSetterEntity get();

    @NonNull
    Observable<JavaGetterSetterEntity> observe();

    @NonNull
    Flowable<JavaGetterSetterEntity> observeWithBackpressure();

    void store(@Nullable JavaGetterSetterEntity entity);
}
