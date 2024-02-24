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

import dev.teogor.drifter.integration.utilities.asString
import dev.teogor.drifter.unity.common.LocalUnityEngine

class UnityDispatcher(
  val gameObject: String,
  val functionName: String,
  val functionParameter: Message,
) {

  init {
    LocalUnityEngine.current.sendMessage(
      gameObject,
      functionName,
      functionParameter.asString,
    )
  }
}
