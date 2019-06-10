package com.kkagurazaka.reactive.repository.sample

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JavaFieldEntityPrefsRepositoryWithoutGetterTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var preferences: SharedPreferences
    private lateinit var repository: JavaFieldEntityPrefsRepositoryWithoutGetter

    @Before
    fun setup() {
        preferences = context.getSharedPreferences("java_field_entity", Context.MODE_PRIVATE)
            .apply { edit().clear().apply() }
        repository = JavaFieldEntityPrefsRepositoryWithoutGetterImpl(context)
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
                    assertThat(someClassList).isEqualTo(listOf(SomeClass("initial")))
                }

                dispose()
            }
    }

    @Test
    fun `observe initially - changed value`() {
        preferences.edit()
            .putBoolean("is_veteran", true)
            .putString("some_str", "some some")
            .putInt("age", 24)
            .putFloat("pie", 3f)
            .putLong("amount", 12L)
            .putStringSet("str_list", setOf("1", "2", "3"))
            .putString("some_class_list", "0,1,2")
            .apply()

        repository.observe()
            .test()
            .run {
                awaitCount(1)

                assertValueCount(1)

                values().first().apply {
                    assertThat(isVeteran).isTrue()
                    assertThat(someStr).isEqualTo("some some")
                    assertThat(age).isEqualTo(24)
                    assertThat(pie).isEqualTo(3f)
                    assertThat(amount).isEqualTo(12L)
                    assertThat(strList).isEqualTo(setOf("1", "2", "3"))
                    assertThat(someClassList).isEqualTo(List(3) { SomeClass("$it") })
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
            someClassList = List(3) { SomeClass("$it") }
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
                assertThat(someClassList).isEqualTo(List(3) { SomeClass("$it") })
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
                    assertThat(someClassList).isEqualTo(listOf(SomeClass("initial")))
                }

                dispose()
            }
    }

    @Test
    fun `observeWithBackpressure initially - changed value`() {
        preferences.edit()
            .putBoolean("is_veteran", true)
            .putString("some_str", "some some")
            .putInt("age", 24)
            .putFloat("pie", 3f)
            .putLong("amount", 12L)
            .putStringSet("str_list", setOf("1", "2", "3"))
            .putString("some_class_list", "0,1,2")
            .apply()

        repository.observeWithBackpressure()
            .test()
            .run {
                awaitCount(1)

                assertValueCount(1)

                values().first().apply {
                    assertThat(isVeteran).isTrue()
                    assertThat(someStr).isEqualTo("some some")
                    assertThat(age).isEqualTo(24)
                    assertThat(pie).isEqualTo(3f)
                    assertThat(amount).isEqualTo(12L)
                    assertThat(strList).isEqualTo(setOf("1", "2", "3"))
                    assertThat(someClassList).isEqualTo(List(3) { SomeClass("$it") })
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
            someClassList = List(3) { SomeClass("$it") }
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
                assertThat(someClassList).isEqualTo(List(3) { SomeClass("$it") })
            }

            dispose()
        }
    }

    @Test
    fun store() {
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

        assertThat(preferences.getBoolean("is_veteran", false)).isTrue()
        assertThat(preferences.getString("some_str", null)).isEqualTo("some some")
        assertThat(preferences.getInt("age", 0)).isEqualTo(24)
        assertThat(preferences.getFloat("pie", 0f)).isEqualTo(3f)
        assertThat(preferences.getLong("amount", 0L)).isEqualTo(12L)
        assertThat(preferences.getStringSet("str_list", null)).isEqualTo(setOf("1", "2", "3"))
        assertThat(preferences.getString("some_class_list", null)).isEqualTo("0,1,2")
    }

    @Test
    fun `store - null`() {
        preferences.edit()
            .putBoolean("is_veteran", true)
            .putString("some_str", "some some")
            .putInt("age", 24)
            .putFloat("pie", 3f)
            .putLong("amount", 12L)
            .putStringSet("str_list", setOf("1", "2", "3"))
            .putString("some_class_list", "0,1,2")
            .apply()

        repository.store(null)

        assertThat(preferences.contains("is_veteran")).isFalse()
        assertThat(preferences.contains("some_str")).isFalse()
        assertThat(preferences.contains("age")).isFalse()
        assertThat(preferences.contains("pie")).isFalse()
        assertThat(preferences.contains("amount")).isFalse()
        assertThat(preferences.contains("str_list")).isFalse()
        assertThat(preferences.contains("some_class_list")).isFalse()
    }
}
