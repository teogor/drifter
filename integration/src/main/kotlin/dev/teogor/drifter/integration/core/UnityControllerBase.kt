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

import dev.teogor.drifter.unity.common.LocalUnityEngine
import org.json.JSONObject

@Deprecated(
  message = "This class is deprecated in favor of UnityMessageSender. Please migrate to the newer class for improved access and functionality.",
  replaceWith = ReplaceWith(
    "UnityMessageSender",
    "dev.teogor.drifter.common.UnityMessageSender",
  ),
)
open class UnityControllerBase(
  private val receiver: String,
) {

  fun invokeAction(methodName: String, data: JSONObject) {
    LocalUnityEngine.current.sendMessage(receiver, methodName, data.toString())
  }
}
