package com.kkagurazaka.reactive.repository.sample

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class KotlinDataClassEntityWithoutDefaultInMemoryRepositoryTest {

    private lateinit var repository: KotlinDataClassEntityWithoutDefaultInMemoryRepository

    @Before
    fun setup() {
        repository = KotlinDataClassEntityWithoutDefaultInMemoryRepositoryImpl()
    }

    @Test
    fun `get - default value`() {
        val result = repository.get()

        assertThat(result).isNull()
    }

    @Test
    fun `store and get`() {
        val newEntity = KotlinDataClassEntityWithoutDefault(
            isVeteran = true,
            someStr = "some some",
            age = 24,
            pie = 3f,
            amount = 12L,
            strList = setOf("1", "2", "3")
        )

        repository.store(newEntity)

        val result = repository.get()

        assertThat(result).isNotNull()
        result!!.run {
            assertThat(isVeteran).isTrue()
            assertThat(someStr).isEqualTo("some some")
            assertThat(age).isEqualTo(24)
            assertThat(pie).isEqualTo(3f)
            assertThat(amount).isEqualTo(12L)
            assertThat(strList).isEqualTo(setOf("1", "2", "3"))
        }
    }

    @Test
    fun `observe initially - never emit`() {
        repository.observe()
            .test()
            .run {
                await(5000, TimeUnit.MILLISECONDS)

                assertNoValues()

                dispose()
            }
    }

    @Test
    fun observe() {
        val tester = repository.observe().test()

        val newEntity = KotlinDataClassEntityWithoutDefault(
            isVeteran = true,
            someStr = "some some",
            age = 24,
            pie = 3f,
            amount = 12L,
            strList = setOf("1", "2", "3")
        )

        repository.store(newEntity)

        tester.run {
            awaitCount(1)

            assertValueCount(1)

            values()[0].apply {
                assertThat(isVeteran).isTrue()
                assertThat(someStr).isEqualTo("some some")
                assertThat(age).isEqualTo(24)
                assertThat(pie).isEqualTo(3f)
                assertThat(amount).isEqualTo(12L)
                assertThat(strList).isEqualTo(setOf("1", "2", "3"))
            }

            dispose()
        }
    }

    @Test
    fun `observeWithBackpressure initially - never emit`() {
        repository.observeWithBackpressure()
            .test()
            .run {
                await(5000, TimeUnit.MILLISECONDS)

                assertNoValues()

                dispose()
            }
    }

    @Test
    fun observeWithBackpressure() {
        val tester = repository.observeWithBackpressure().test()

        val newEntity = KotlinDataClassEntityWithoutDefault(
            isVeteran = true,
            someStr = "some some",
            age = 24,
            pie = 3f,
            amount = 12L,
            strList = setOf("1", "2", "3")
        )

        repository.store(newEntity)

        tester.run {
            awaitCount(1)

            assertValueCount(1)

            values()[0].apply {
                assertThat(isVeteran).isTrue()
                assertThat(someStr).isEqualTo("some some")
                assertThat(age).isEqualTo(24)
                assertThat(pie).isEqualTo(3f)
                assertThat(amount).isEqualTo(12L)
                assertThat(strList).isEqualTo(setOf("1", "2", "3"))
            }

            dispose()
        }
    }
}