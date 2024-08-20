/*
 * Copyright 2023 teogor (Teodor Grigor)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.teogor.drifter.integration.core

import android.content.Context
import android.content.SharedPreferences
import dev.teogor.drifter.integration.initializer.ActivityContextProvider.applicationContext

open class PlayerPrefs protected constructor() {
  private val keySharedPrefs: String = applicationContext.packageName + ".v2.playerprefs"

  private val sharedPreferences: SharedPreferences
    get() = applicationContext.getSharedPreferences(keySharedPrefs, Context.MODE_PRIVATE)

  operator fun contains(key: String?): Boolean {
    return sharedPreferences.contains(key)
  }

  fun setInt(key: String?, value: Int) {
    sharedPreferences.edit().putInt(key, value).apply()
  }

  fun getInt(key: String?, defValue: Int): Int {
    return sharedPreferences.getInt(key, defValue)
  }

  fun setFloat(key: String?, value: Float) {
    sharedPreferences.edit().putFloat(key, value).apply()
  }

  fun getFloat(key: String?, defValue: Float): Float {
    return sharedPreferences.getFloat(key, defValue)
  }

  fun setLong(key: String?, value: Long) {
    sharedPreferences.edit().putLong(key, value).apply()
  }

  fun getLong(key: String?, defValue: Long): Long {
    return sharedPreferences.getLong(key, defValue)
  }

  fun setString(key: String?, value: String?) {
    sharedPreferences.edit().putString(key, value).apply()
  }

  fun getString(key: String?, defValue: String?): String? {
    return sharedPreferences.getString(key, defValue)
  }

  fun setBoolean(key: String?, value: Boolean) {
    sharedPreferences.edit().putInt(key, if (value) 1 else 0).apply()
  }

  fun getBoolean(key: String?, defValue: Boolean): Boolean {
    return sharedPreferences.getInt(key, if (defValue) 1 else 0) == 1
  }

  fun toggleBoolean(key: String?, defValue: Boolean): Boolean {
    val value = sharedPreferences.getInt(key, if (defValue) 1 else 0) != 1
    setBoolean(key, value)
    return value
  }

  companion object {
    @JvmStatic
    val instance: PlayerPrefs
      get() = PlayerPrefs()
  }
}
