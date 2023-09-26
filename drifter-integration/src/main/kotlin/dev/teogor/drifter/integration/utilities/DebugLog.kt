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

package dev.teogor.drifter.integration.utilities

import android.util.Log
import dev.teogor.drifter.integration.BuildConfig

/**
 * Simple Log wrapper for internal usage.
 */
object DebugLog {
  private val ENABLED = BuildConfig.DEBUG
  private val VERBOSE_ENABLED = BuildConfig.DEBUG
  private const val TAG = "DebugLog"
  private var sStartupMessageLogged = false

  @JvmStatic
  fun d(obj: Any?) {
    if (!ENABLED) return
    Log.d(TAG, getMessage(obj))
  }

  @JvmStatic
  fun v(obj: Any?) {
    if (!VERBOSE_ENABLED) return
    Log.v(TAG, getMessage(obj))
  }

  @JvmStatic
  fun e(obj: Any?) {
    if (!ENABLED) return
    Log.e(TAG, getMessage(obj))
  }

  @JvmStatic
  fun logStartupMessage() {
    if (sStartupMessageLogged) return
    sStartupMessageLogged = true
  }

  private fun getMessage(obj: Any?): String {
    return obj?.toString() ?: "null"
  }
}
