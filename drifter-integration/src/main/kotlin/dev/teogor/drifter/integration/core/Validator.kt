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
import android.widget.Toast
import dev.teogor.drifter.integration.initializer.ActivityContextProvider.applicationContext

class Validator {
  @get:UnityCallback
  var isDataChanged = false
    private set

  @UnityCallback
  fun dataChangedRead() {
    isDataChanged = false
  }

  @UnityCallback
  fun setDataChanged() {
    isDataChanged = true
  }

  @get:UnityCallback
  val context: Context?
    get() = try {
      applicationContext
    } catch (ignored: Exception) {
      null
    }

  @UnityCallback
  fun markAsRead(element: String) {
    writeElement(element, null)
  }

  @UnityCallback
  fun getStorageElement(key: String): String? {
    return PlayerPrefs.instance.getString(
      key,
      null,
    )
  }

  @Suppress("UNCHECKED_CAST")
  @UnityCallback
  fun <T> getStorageElement(
    key: String,
    defaultValue: T,
  ): T {
    return when (defaultValue) {
      is Int -> PlayerPrefs.instance.getInt(
        key,
        defaultValue,
      ) as? T ?: defaultValue
      else -> throw RuntimeException("Invalid t format")
    }
  }

  @UnityCallback
  fun toast(content: String?) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
  }

  fun writeElement(key: String, content: String?) {
    PlayerPrefs.instance.setString(key, content)
  }

  fun writeElement(key: String, content: Int) {
    PlayerPrefs.instance.setInt(key, content)
  }

  companion object {
    private var validatorInstance: Validator? = null
    val instance: Validator by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
      validatorInstance ?: Validator().also {
        validatorInstance = it
      }
    }
  }
}
