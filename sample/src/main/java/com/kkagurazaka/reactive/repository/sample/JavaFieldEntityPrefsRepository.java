package com.kkagurazaka.reactive.repository.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.kkagurazaka.reactive.repository.annotation.PrefsRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@PrefsRepository(JavaFieldEntity.class)
public interface JavaFieldEntityPrefsRepository {

    @NonNull
    JavaFieldEntity get();

    @NonNull
    Observable<JavaFieldEntity> observe();

    @NonNull
    Flowable<JavaFieldEntity> observeWithBackpressure();

    void store(@Nullable JavaFieldEntity entity);
}
