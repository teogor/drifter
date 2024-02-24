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

import android.app.Activity
import android.content.res.Configuration
import android.view.InputEvent
import android.view.Surface

/**
 * Defines essential methods for interacting with a Unity Player instance.
 *
 * Implement this interface to provide the core functionalities required
 * to manage the Unity Player's lifecycle, handle events, and respond to system changes.
 */
interface IUnityPlayer {

  // ----------------------------------------
  // Lifecycle Methods
  // ----------------------------------------

  /**
   * Pauses the Unity Player, suspending its execution.
   */
  fun pause()

  /**
   * Resumes the Unity Player, allowing it to continue execution.
   */
  fun resume()

  /**
   * Quits the Unity Player, gracefully terminating its operation.
   */
  fun quit()

  /**
   * Unloads the Unity Player, releasing resources but maintaining its state.
   */
  fun unload()

  // ----------------------------------------
  // Resource Management Methods
  // ----------------------------------------

  /**
   * Notifies the Unity Player of a low-memory condition.
   * It should take appropriate actions to release unnecessary resources.
   */
  fun lowMemory()

  /**
   * Forcefully terminates the Unity Player, potentially discarding unsaved state.
   */
  fun kill()

  /**
   * Destroys the Unity Player, releasing all associated resources.
   */
  fun destroy()

  // ----------------------------------------
  // Event Handling Methods
  // ----------------------------------------

  /**
   * Called when the window containing the Unity Player gains or loses focus.
   *
   * @param hasFocus True if the window has gained focus, false if it has lost focus.
   */
  fun windowFocusChanged(hasFocus: Boolean)

  /**
   * Injects an input event into the Unity Player for processing.
   *
   * @param inputEvent The input event to inject.
   * @return True if the event was consumed by the Unity Player, false otherwise.
   */
  fun injectEvent(inputEvent: InputEvent?): Boolean

  // ----------------------------------------
  // System Configuration Methods
  // ----------------------------------------

  /**
   * Called when the device configuration changes, such as screen orientation or language.
   *
   * @param configuration The new configuration.
   */
  fun configurationChanged(configuration: Configuration?)

  /**
   * Called when the display configuration changes.
   *
   * @param displayId The ID of the affected display.
   * @param updatedSurface The updated surface for rendering, if applicable.
   * @return True if the Unity Player can handle the display change, false otherwise.
   */
  fun displayChanged(displayId: Int, updatedSurface: Surface?): Boolean

  /**
   * Called when a new Activity becomes the current foreground activity.
   *
   * @param activity The new current Activity.
   */
  fun onNewCurrentActivity(activity: Activity)
}
