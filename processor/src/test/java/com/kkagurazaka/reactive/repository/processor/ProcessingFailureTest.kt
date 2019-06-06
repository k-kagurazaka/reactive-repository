package com.kkagurazaka.reactive.repository.processor

import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.Test

class ProcessingFailureTest {

    @Test
    fun `entity is not annotated with @InMemoryEntity`() {
        assertProcessingFailure(
            sources = listOf(
                "NotAnnotatedEntity.java",
                "NotAnnotatedEntityInMemoryRepository.java"
            ),
            errorContaining = "NotAnnotatedEntity is not annotated with @InMemoryEntity"
        )
    }

    @Test
    fun `multiple getters`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityMultipleGettersRepository.java"
            ),
            errorContaining = "Multiple getters found in InMemoryFieldEntityMultipleGettersRepository"
        )
    }

    @Test
    fun `multiple setters`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityMultipleSettersRepository.java"
            ),
            errorContaining = "Multiple setters found in InMemoryFieldEntityMultipleSettersRepository"
        )
    }

    @Test
    fun `@NonNull getters found but empty constructor does not found`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryGetterConstructorEntity.java",
                "InMemoryGetterConstructorEntityNonNullGetterRepository.java"
            ),
            errorContaining = "@NonNull getter found in InMemoryGetterConstructorEntityNonNullGetterRepository but InMemoryGetterConstructorEntity does not have a constructor with no parameters"
        )
    }

    @Test
    fun `getter returning not target entity`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryNotTargetEntity.java",
                "InMemoryFieldEntityNotTargetGetterRepository.java"
            ),
            errorContaining = "Expected return type is InMemoryFieldEntity, Observable<InMemoryFieldEntity> or Flowable<InMemoryFieldEntity> but actual is InMemoryNotTargetEntity at get()"
        )
    }

    @Test
    fun `return @Nullable Observable`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityNullableObservableRepository.java"
            ),
            errorContaining = "Method returning Observable cannot be annotated with @Nullable"
        )
    }

    @Test
    fun `return Observable of not target entity`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryNotTargetEntity.java",
                "InMemoryFieldEntityNotTargetObservableRepository.java"
            ),
            errorContaining = "Expected return type is Observable<InMemoryFieldEntity> but actual is Observable<InMemoryNotTargetEntity> at observe()"
        )
    }

    @Test
    fun `return @Nullable Flowable`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityNullableFlowableRepository.java"
            ),
            errorContaining = "Method returning Flowable cannot be annotated with @Nullable"
        )
    }

    @Test
    fun `return Flowable of not target entity`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryNotTargetEntity.java",
                "InMemoryFieldEntityNotTargetFlowableRepository.java"
            ),
            errorContaining = "Expected return type is Flowable<InMemoryFieldEntity> but actual is Flowable<InMemoryNotTargetEntity> at observe()"
        )
    }

    @Test
    fun `unexpected method type`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityUnsupportedMethodRepository.java"
            ),
            errorContaining = "Signature of store is not supported"
        )
    }

    @Test
    fun `@NonNull getter and @Nullable setter at @InMemoryRepository`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityNonNullGetterNullableSetterRepository.java"
            ),
            errorContaining = "@InMemoryRepository does not accept both of @NonNull getter and @Nullable setter"
        )
    }

    @Test
    fun `@Nullable setter and Rx2 integration`() {
        assertProcessingFailure(
            sources = listOf(
                "InMemoryFieldEntity.java",
                "InMemoryFieldEntityNullableSetterWithRx2Repository.java"
            ),
            errorContaining = "@InMemoryRepository does not accept @Nullable setter with RxJava2 integration"
        )
    }

    @Test
    fun `entity is not annotated with @PrefsEntity`() {
        assertProcessingFailure(
            sources = listOf(
                "NotAnnotatedEntity.java",
                "NotAnnotatedEntityPrefsRepository.java"
            ),
            errorContaining = "NotAnnotatedEntity is not annotated with @PrefsEntity"
        )
    }

    @Test
    fun `empty constructor does not found in @PrefsEntity`() {
        assertProcessingFailure(
            sources = listOf("PrefsGetterConstructorEntity.java"),
            errorContaining = "@PrefsEntity requires a constructor with no parameters"
        )
    }

    @Test
    fun `@Nullable getter at @PrefsRepository`() {
        assertProcessingFailure(
            sources = listOf(
                "PrefsFieldEntity.java",
                "PrefsFieldEntityNullableGetterRepository.java"
            ),
            errorContaining = "@PrefsRepository does not accept @Nullable getter"
        )
    }

    @Test
    fun `not supported type by SharedPreferences`() {
        assertProcessingFailure(
            sources = listOf(
                "PrefsUnsupportedTypeEntity.java",
                "PrefsUnsupportedTypeEntityRepository.java"
            ),
            errorContaining = "double is not supported by SharedPreferences"
        )
    }

    @Test
    fun `conflict preferenceName and useDefaultPreferences`() {
        assertProcessingFailure(
            sources = listOf("PrefsConflictEntity.java"),
            errorContaining = "preferenceName is set but useDefaultPreferences = true"
        )
    }

    @Test
    fun `mix field and getter setter in @PrefsEntity`() {
        assertProcessingFailure(
            sources = listOf("PrefsMixEntity.java"),
            errorContaining = "Cannot annotate field and getter / setter with @PrefsKey together"
        )
    }

    @Test
    fun `no setter in @PrefsEntity`() {
        assertProcessingFailure(
            sources = listOf("PrefsNoSetterEntity.java"),
            errorContaining = "No setters annotated with @PrefsKey found in @PrefsEntity"
        )
    }

    @Test
    fun `unmatch getter and setter`() {
        assertProcessingFailure(
            sources = listOf("PrefsUnmatchGetterSetterEntity.java"),
            errorContaining = "Getters and setters do not match"
        )
    }

    @Test
    fun `no field`() {
        assertProcessingFailure(
            sources = listOf("PrefsNoFieldEntity.java"),
            errorContaining = "Public non-final field or getter / setter annotated with @PrefsKey is not found"
        )
    }

    private fun assertProcessingFailure(sources: List<String>, errorContaining: String) {
        assert_().about(JavaSourcesSubjectFactory.javaSources())
            .that(sources.map { JavaFileObjects.forResource(it) })
            .processedWith(ReactiveRepositoryProcessor())
            .failsToCompile()
            .withErrorCount(1)
            .withErrorContaining(errorContaining)
    }
}
