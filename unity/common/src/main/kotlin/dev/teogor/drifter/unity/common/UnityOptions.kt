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
import java.lang.reflect.Field

/**
 * Encapsulates configuration options for customizing Unity Player instances.
 *
 * @property detachFromActivity Determines whether to detach the Unity Player from its
 * associated Activity.
 * @property contextFieldConfigurator A lambda for custom configuration of context-related
 * fields.
 */
class UnityOptions {
  var detachFromActivity: Boolean = false
    private set
  var contextFieldConfigurator: (Field) -> Unit = {}
    private set

  /**
   * Enables or disables detaching the Unity Player from its Activity.
   *
   * @param detach True to detach, false to maintain Activity association.
   */
  fun setDetachFromActivity(detach: Boolean) {
    detachFromActivity = detach
  }

  /**
   * Sets a custom function for configuring context-related fields.
   *
   * @param block The lambda function for custom context field configuration.
   */
  fun setContextFieldConfigurator(block: (Field) -> Unit) {
    contextFieldConfigurator = block
  }
}

/**
 * Configures a [BaseUnityPlayer] instance with the provided options and context.
 *
 * @param options [UnityOptions] instance containing configuration settings.
 * @param contextWrapper The Android context wrapper.
 * @param block A lambda block for configuring the options within a concise scope.
 *
 * @return The [BaseUnityPlayer] instance itself, for fluent usage.
 */
fun BaseUnityPlayer.configureOptions(
  options: UnityOptions,
  contextWrapper: ContextWrapper,
  block: UnityOptions.() -> Unit,
) = apply {
  applyOptions(options.apply(block), contextWrapper)
}
