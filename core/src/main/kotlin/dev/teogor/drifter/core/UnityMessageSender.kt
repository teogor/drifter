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

package dev.teogor.drifter.core

import dev.teogor.drifter.unity.common.UnityEngine
import org.json.JSONObject

/**
 * Sends messages to a Unity game object.
 *
 * This class provides a convenient way to send messages to a specific game object
 * within your Unity project from your Android code. You can pass method names and JSON data
 * to trigger actions in Unity.
 *
 * @param receiver The name of the Unity game object to receive the messages.
 */
open class UnityMessageSender(private val receiver: String) {

  /**
   * Sends a message to the specified game object with a method name and JSON data.
   *
   * @param methodName The name of the method to invoke in the Unity game object.
   * @param data The JSON data to send as arguments to the method.
   */
  fun sendMessage(methodName: String, data: JSONObject) {
    UnityEngine.sendMessage(receiver, methodName, data.toString())
  }
}
