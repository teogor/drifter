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

import android.content.ContextWrapper
import dev.teogor.ceres.core.register.intrinsicImplementation
import dev.teogor.ceres.core.register.staticRegistryLocalOf

/**
 * Factory for creating Unity Player instances.
 *
 * This interface allows for the creation of Unity Players of different versions or
 * implementations. It encapsulates the logic of selecting and instantiating the
 * appropriate Unity Player based on specific requirements or configurations.
 */
interface UnityEngineFactory {
  /**
   * The version of Unity supported by this factory.
   */
  val version: String

  /**
   * Creates a new Unity Player instance.
   * Creates a new Unity Player instance, optionally applying configuration options.
   *
   * @param contextWrapper The Android context wrapper.
   * @param options [UnityOptions] instance for customizing the created Unity Player.
   * Defaults to an empty [UnityOptions] instance.
   *
   * @return The created Unity Player instance, configured according to the provided options.
   */
  fun createUnityPlayer(
    contextWrapper: ContextWrapper,
    options: UnityOptions = UnityOptions(),
  ): IUnityPlayer

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
  )
}

/**
 * Provides access to the current [UnityEngineFactory] instance.
 *
 * This static registry holds the [UnityEngineFactory] instance that is responsible
 * for creating Unity Players within the application. It allows components to access
 * and use the factory without the need for manual dependency injection or configuration.
 */
val LocalUnityEngine = staticRegistryLocalOf<UnityEngineFactory> {
  intrinsicImplementation<UnityEngineFactory>()
}
