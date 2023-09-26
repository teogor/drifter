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

/**
 * Uncaught exception handler that logs the exception to System.err stream.
 * Note: Unity takes over the default uncaught exception handler. On some devices
 * this results to stack traces not being printed partially or entirely.
 */
class StandardExceptionHandler private constructor() : Thread.UncaughtExceptionHandler {
  private val mDefaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler?
    get() = Thread.getDefaultUncaughtExceptionHandler()
  private var mIsAttached = false

  /**
   * Sets `StandardExceptionHandler` as default uncaught exception handler.
   */
  fun attach() {
    // Only try to attach once
    if (mIsAttached) return
    Thread.setDefaultUncaughtExceptionHandler(this)
    mIsAttached = true
  }

  override fun uncaughtException(thread: Thread, ex: Throwable) {
    // Log the exception to System.err and forward the exception to saved default handler
    System.err.print(Log.getStackTraceString(ex))
    mDefaultUncaughtExceptionHandler?.uncaughtException(thread, ex)
  }

  companion object {
    val instance = StandardExceptionHandler()
  }
}
