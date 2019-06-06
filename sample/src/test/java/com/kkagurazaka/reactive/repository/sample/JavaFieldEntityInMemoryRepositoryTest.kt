package com.kkagurazaka.reactive.repository.sample

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class JavaFieldEntityInMemoryRepositoryTest {

    private lateinit var repository: JavaFieldEntityInMemoryRepository

    @Before
    fun setup() {
        repository = JavaFieldEntityInMemoryRepositoryImpl()
    }

    @Test
    fun `get - default value`() {
        val result = repository.get()

        assertThat(result).isNotNull()
        result!!.run {
            assertThat(isVeteran).isFalse()
            assertThat(someStr).isNull()
            assertThat(age).isEqualTo(-1)
            assertThat(pie).isEqualTo(3.1415f)
            assertThat(amount).isEqualTo(123456789L)
            assertThat(strList).isEmpty()
        }
    }

    @Test
    fun `store and get`() {
        val newEntity = JavaFieldEntity().apply {
            isVeteran = true
            someStr = "some some"
            age = 24
            pie = 3f
            amount = 12L
            strList = setOf("1", "2", "3")
        }

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
    fun `observe initially - default value`() {
        repository.observe()
            .test()
            .run {
                awaitCount(1)

                assertValueCount(1)

                values().first().apply {
                    assertThat(isVeteran).isFalse()
                    assertThat(someStr).isNull()
                    assertThat(age).isEqualTo(-1)
                    assertThat(pie).isEqualTo(3.1415f)
                    assertThat(amount).isEqualTo(123456789L)
                    assertThat(strList).isEmpty()
                }

                dispose()
            }
    }

    @Test
    fun observe() {
        val tester = repository.observe().test()

        val newEntity = JavaFieldEntity().apply {
            isVeteran = true
            someStr = "some some"
            age = 24
            pie = 3f
            amount = 12L
            strList = setOf("1", "2", "3")
        }

        repository.store(newEntity)

        tester.run {
            awaitCount(2)

            assertValueCount(2)

            values()[1].apply {
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
    fun `observeWithBackpressure initially - default value`() {
        repository.observeWithBackpressure()
            .test()
            .run {
                awaitCount(1)

                assertValueCount(1)

                values().first().apply {
                    assertThat(isVeteran).isFalse()
                    assertThat(someStr).isNull()
                    assertThat(age).isEqualTo(-1)
                    assertThat(pie).isEqualTo(3.1415f)
                    assertThat(amount).isEqualTo(123456789L)
                    assertThat(strList).isEmpty()
                }

                dispose()
            }
    }

    @Test
    fun observeWithBackpressure() {
        val tester = repository.observeWithBackpressure().test()

        val newEntity = JavaFieldEntity().apply {
            isVeteran = true
            someStr = "some some"
            age = 24
            pie = 3f
            amount = 12L
            strList = setOf("1", "2", "3")
        }

        repository.store(newEntity)

        tester.run {
            awaitCount(2)

            assertValueCount(2)

            values()[1].apply {
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
