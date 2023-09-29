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

import dev.teogor.drifter.integration.common.TypeConverter

open class UnityStorageBase {
  protected open fun <T> writeElement(
    key: String,
    content: T,
    converter: TypeConverter<T>? = null,
  ): T {
    when {
      converter != null -> PlayerPrefs.instance.setString(
        key = key,
        value = converter.convertToString(content),
      )

      content is String -> PlayerPrefs.instance.setString(
        key = key,
        value = content,
      )

      content is Int -> PlayerPrefs.instance.setInt(
        key = key,
        value = content,
      )

      else -> throw IllegalArgumentException("Invalid type for content")
    }
    return content
  }

  @Suppress("UNCHECKED_CAST")
  @UnityCallback
  protected open fun <T> getStorageElement(
    key: String,
    defaultValue: T,
    converter: TypeConverter<T>? = null,
  ): T {
    return when {
      converter != null -> PlayerPrefs.instance.getString(
        key = key,
        defValue = null,
      )?.let { converter.convertFromString(it) } ?: defaultValue

      defaultValue is String -> PlayerPrefs.instance.getString(
        key = key,
        defValue = defaultValue,
      ) as? T ?: defaultValue

      defaultValue is Int -> PlayerPrefs.instance.getInt(
        key = key,
        defValue = defaultValue,
      ) as? T ?: defaultValue

      else -> defaultValue
    }
  }
}
