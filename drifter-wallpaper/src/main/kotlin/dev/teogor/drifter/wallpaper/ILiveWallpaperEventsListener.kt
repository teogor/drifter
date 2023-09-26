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

package dev.teogor.drifter.wallpaper

/**
 * Describes all events that are sent from Java side to Unity.
 */
interface ILiveWallpaperEventsListener {
  /**
   * Called to inform whether the wallpaper has become visible or not visible.
   *
   * @param isVisible
   */
  fun visibilityChanged(isVisible: Boolean)

  /**
   * Called to inform whether the wallpaper has entered or exited the preview mode.
   *
   * @param isPreview
   */
  fun isPreviewChanged(isPreview: Boolean)

  /**
   * Called to inform about the change of wallpaper desired size.
   *
   * @param desiredWidth
   * @param desiredHeight
   */
  fun desiredSizeChanged(desiredWidth: Int, desiredHeight: Int)

  /**
   * Called to inform about change of wallpaper offsets.
   *
   * @param xOffset
   * @param yOffset
   * @param xOffsetStep
   * @param yOffsetStep
   * @param xPixelOffset
   * @param yPixelOffset
   */
  fun offsetsChanged(
    xOffset: Float,
    yOffset: Float,
    xOffsetStep: Float,
    yOffsetStep: Float,
    xPixelOffset: Int,
    yPixelOffset: Int,
  )

  /**
   * Called to inform about the change of preference value.
   *
   * @param key SharedPreferences preference key that has changed.
   */
  fun preferenceChanged(key: String)

  /**
   * Called to inform that live wallpaper preferences Activity has started.
   */
  fun preferencesActivityTriggered()

  /**
   * Called to inform about a custom event.
   *
   * @param eventName Name of the event.
   * @param eventData Event data.
   */
  fun customEventReceived(eventName: String, eventData: String)

  /**
   * Called to inform that user has tapped the screen multiple times.
   */
  fun multiTapDetected(finalTapPositionX: Float, finalTapPositionY: Float)
}
