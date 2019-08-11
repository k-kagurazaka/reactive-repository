package com.kkagurazaka.reactive.repository.processor.tools

import com.kkagurazaka.reactive.repository.annotation.PrefsEntity
import com.kkagurazaka.reactive.repository.annotation.PrefsKey
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName

object Types {

    val annotationNonNullJetBrains: ClassName =
        ClassName.get("org.jetbrains.annotations", "NotNull")
    val annotationNullableJetBrains: ClassName =
        ClassName.get("org.jetbrains.annotations", "Nullable")
    val annotationNonNullAndroidX: ClassName =
        ClassName.get("androidx.annotation", "NonNull")
    val annotationNullableAndroidX: ClassName =
        ClassName.get("androidx.annotation", "Nullable")
    val annotationNonNullSupport: ClassName =
        ClassName.get("android.support.annotation", "NonNull")
    val annotationNullableSupport: ClassName =
        ClassName.get("android.support.annotation", "Nullable")

    val string: ClassName =
        ClassName.get(String::class.java)
    val stringSet: ParameterizedTypeName =
        ParameterizedTypeName.get(Set::class.java, String::class.java)

    val androidContext: ClassName =
        ClassName.get("android.content", "Context")
    val sharedPreferences: ClassName =
        ClassName.get("android.content", "SharedPreferences")
    val preferenceManager: ClassName =
        ClassName.get("android.preference", "PreferenceManager")

    val rx2Observable: ClassName =
        ClassName.get("io.reactivex", "Observable")
    val rx2Flowable: ClassName =
        ClassName.get("io.reactivex", "Flowable")
    val rx2BehaviorProcessor: ClassName =
        ClassName.get("io.reactivex.processors", "BehaviorProcessor")
    val rx2FlowableProcessor: ClassName =
        ClassName.get("io.reactivex.processors", "FlowableProcessor")

    val prefsKey: ClassName =
        ClassName.get(PrefsKey::class.java)
    val defaultTypeAdapter: ClassName =
        ClassName.get(PrefsEntity.DEFAULT_ADAPTER::class.java)

    fun atomicReference(className: ClassName): ParameterizedTypeName =
        ParameterizedTypeName.get(
            ClassName.get("java.util.concurrent.atomic", "AtomicReference"),
            className
        )
}
