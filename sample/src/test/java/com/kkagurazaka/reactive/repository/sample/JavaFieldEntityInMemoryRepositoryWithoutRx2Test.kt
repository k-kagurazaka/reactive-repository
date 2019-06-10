package com.kkagurazaka.reactive.repository.sample

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class JavaFieldEntityInMemoryRepositoryWithoutRx2Test {

    private lateinit var repository: JavaFieldEntityInMemoryRepositoryWithoutRx2

    @Before
    fun setup() {
        repository = JavaFieldEntityInMemoryRepositoryWithoutRx2Impl()
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
            assertThat(someClassList).isEqualTo(listOf(SomeClass("initial")))
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
            someClassList = List(3) { SomeClass("$it") }
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
            assertThat(someClassList).isEqualTo(List(3) { SomeClass("$it") })
        }
    }
}
