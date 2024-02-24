/*
 * Copyright 2024 teogor (Teodor Grigor)
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

package dev.teogor.drifter.ksp.codegen

import com.google.devtools.ksp.processing.KSPLogger
import dev.teogor.drifter.codegen.facades.Logger

class KspLogger(
  private val kspLogger: KSPLogger,
) : Logger {

  override fun logging(message: String) = kspLogger.logging(message)

  override fun info(message: String) = kspLogger.info(message)

  override fun warn(message: String) = kspLogger.warn(message)

  override fun error(message: String) = kspLogger.error(message)

  override fun exception(e: Throwable) = kspLogger.exception(e)
}
