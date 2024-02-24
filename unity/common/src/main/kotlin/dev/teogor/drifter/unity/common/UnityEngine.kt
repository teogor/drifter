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

package dev.teogor.drifter.unity.common

/**
 * Provides a single point of access for interacting with the Unity Player instance.
 *
 * This object serves as a facade, delegating all calls to the currently registered
 * [UnityEngineFactory] through the [LocalUnityEngine] registry.
 *
 * Use this object to send messages to Unity GameObjects, access native Unity functionalities,
 * or perform other Unity-related operations from your Kotlin code.
 */
object UnityEngine {

  /**
   * Sends a message to a specific GameObject within the Unity Player instance,
   * calling the specified method with the provided parameter.
   *
   * @param gameObject The name of the GameObject to send the message to.
   * @param methodName The name of the method to invoke on the GameObject.
   * @param funcParam The parameter to pass to the invoked method, as a string.
   */
  fun sendMessage(
    gameObject: String,
    methodName: String,
    funcParam: String,
  ) = LocalUnityEngine.current.sendMessage(gameObject, methodName, funcParam)
}
