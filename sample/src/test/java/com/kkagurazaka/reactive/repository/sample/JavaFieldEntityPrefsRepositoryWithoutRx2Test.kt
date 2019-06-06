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
class JavaFieldEntityPrefsRepositoryWithoutRx2Test {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var preferences: SharedPreferences
    private lateinit var repository: JavaFieldEntityPrefsRepositoryWithoutRx2

    @Before
    fun setup() {
        preferences = context.getSharedPreferences("java_field_entity", Context.MODE_PRIVATE)
            .apply { edit().clear().apply() }
        repository = JavaFieldEntityPrefsRepositoryWithoutRx2Impl(context)
    }

    @Test
    fun `get - default value`() {
        val result = repository.get()

        result.run {
            assertThat(isVeteran).isFalse()
            assertThat(someStr).isNull()
            assertThat(age).isEqualTo(-1)
            assertThat(pie).isEqualTo(3.1415f)
            assertThat(amount).isEqualTo(123456789L)
            assertThat(strList).isEmpty()
        }
    }

    @Test
    fun `get - changed value`() {
        preferences.edit()
            .putBoolean("is_veteran", true)
            .putString("some_str", "some some")
            .putInt("age", 24)
            .putFloat("pie", 3f)
            .putLong("amount", 12L)
            .putStringSet("str_list", setOf("1", "2", "3"))
            .apply()

        val result = repository.get()

        result.run {
            assertThat(isVeteran).isTrue()
            assertThat(someStr).isEqualTo("some some")
            assertThat(age).isEqualTo(24)
            assertThat(pie).isEqualTo(3f)
            assertThat(amount).isEqualTo(12L)
            assertThat(strList).isEqualTo(setOf("1", "2", "3"))
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
        }

        repository.store(newEntity)

        assertThat(preferences.getBoolean("is_veteran", false)).isTrue()
        assertThat(preferences.getString("some_str", null)).isEqualTo("some some")
        assertThat(preferences.getInt("age", 0)).isEqualTo(24)
        assertThat(preferences.getFloat("pie", 0f)).isEqualTo(3f)
        assertThat(preferences.getLong("amount", 0L)).isEqualTo(12L)
        assertThat(preferences.getStringSet("str_list", null)).isEqualTo(setOf("1", "2", "3"))
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
            .apply()

        repository.store(null)

        assertThat(preferences.contains("is_veteran")).isFalse()
        assertThat(preferences.contains("some_str")).isFalse()
        assertThat(preferences.contains("age")).isFalse()
        assertThat(preferences.contains("pie")).isFalse()
        assertThat(preferences.contains("amount")).isFalse()
        assertThat(preferences.contains("str_list")).isFalse()
    }
}
