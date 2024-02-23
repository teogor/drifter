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

package dev.teogor.drifter.common

import dev.teogor.drifter.integration.core.PlayerPrefs

/**
 * Provides a storage layer for Unity using PlayerPrefs.
 *
 * This class offers convenient methods to save and retrieve various data types
 * using PlayerPrefs, allowing communication between Android and Unity.
 */
open class UnityPlayerPrefs {

  /**
   * Saves a value with the given key and performs type conversion if necessary.
   *
   * @param key The key to store the value under.
   * @param value The value to save.
   * @param converter Optional type converter if needed.
   */
  @UnityPrefAccess
  protected open fun <T> set(
    key: String,
    value: T,
    converter: Serializable<T>? = null,
  ): T {
    when {
      converter != null -> PlayerPrefs.instance.setString(
        key = key,
        value = converter.encodeToStringImpl(value),
      )

      value is String -> PlayerPrefs.instance.setString(
        key = key,
        value = value,
      )

      value is Int -> PlayerPrefs.instance.setInt(
        key = key,
        value = value,
      )

      value is Boolean -> PlayerPrefs.instance.setInt(
        key = key,
        value = if (value) 1 else 0,
      )

      else -> throw IllegalArgumentException("Invalid type for value.")
    }
    return value
  }

  /**
   * Retrieves a value with the given key and performs type conversion if necessary.
   *
   * @param key The key to retrieve the value from.
   * @param defaultValue The default value to return if the key is not found.
   * @param converter Optional type converter if needed.
   * @return The retrieved value or the default value if not found.
   */
  @Suppress("UNCHECKED_CAST")
  @UnityPrefAccess
  protected open fun <T> get(
    key: String,
    defaultValue: T,
    converter: Serializable<T>? = null,
  ): T {
    return when {
      converter != null -> PlayerPrefs.instance.getString(
        key = key,
        defValue = null,
      )?.let { converter.decodeFromStringImpl(it) } ?: defaultValue

      defaultValue is String -> PlayerPrefs.instance.getString(
        key = key,
        defValue = defaultValue,
      ) as? T ?: defaultValue

      defaultValue is Boolean -> {
        val storedValueInt = PlayerPrefs.instance.getInt(
          key = key,
          defValue = if (defaultValue) 1 else 0,
        )
        (storedValueInt == 1) as T
      }

      defaultValue is Int -> PlayerPrefs.instance.getInt(
        key = key,
        defValue = defaultValue,
      ) as? T ?: defaultValue

      else -> defaultValue
    }
  }
}
