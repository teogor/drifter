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

object DataSync {
  /**
   * Methods
   */
  const val STATUS_BAR_SCRIPT = "StatusBarManager"
  const val STATUS_BAR_SET_ENABLED = STATUS_BAR_SCRIPT + "_SetEnabled"
  const val STATUS_BAR_SET_DARK = STATUS_BAR_SCRIPT + "_SetDark"
  const val STATUS_BAR_SET_OPACITY = STATUS_BAR_SCRIPT + "_SetOpacity"
  const val STATUS_BAR_SET_HEIGHT = STATUS_BAR_SCRIPT + "_SetHeight"
  const val NAVIGATION_BAR_SCRIPT = "NavigationBarManager"
  const val NAVIGATION_BAR_SET_ENABLED = NAVIGATION_BAR_SCRIPT + "_SetEnabled"
  const val NAVIGATION_BAR_SET_DARK = NAVIGATION_BAR_SCRIPT + "_SetDark"
  const val NAVIGATION_BAR_SET_OPACITY = NAVIGATION_BAR_SCRIPT + "_SetOpacity"
  const val NAVIGATION_BAR_SET_HEIGHT = NAVIGATION_BAR_SCRIPT + "_SetHeight"

  /**
   * Fields from [PlayerPrefs]
   */
  const val DEBUG_ENABLED = "isDebug"
  const val EDITOR_ENABLED = "isEditorEnabled"

  /**
   * TODO beta
   * - status bar
   * - navigation bar
   */
  const val STATUS_BAR_PREFS_ENABLED = "statusBarIsEnabled"
  const val STATUS_BAR_PREFS_DARK = "statusBarIsDark"
  const val STATUS_BAR_PREFS_OPACITY = "statusBarOpacity"
  const val STATUS_BAR_PREFS_HEIGHT = "statusBarHeight"
  const val NAVIGATION_BAR_PREFS_ENABLED = "navigationBarIsEnabled"
  const val NAVIGATION_BAR_PREFS_DARK = "navigationBarIsDark"
  const val NAVIGATION_BAR_PREFS_OPACITY = "navigationBarOpacity"
  const val NAVIGATION_BAR_PREFS_HEIGHT = "navigationBarHeight"
  const val WATER_COLOUR = "waterColour"
  const val DATA_SYNC_EMPTY = "DATA_SYNC_EMPTY"
  const val COLOUR_CHANGED = "WallpaperSettings_ChangeColour"
  const val CYCLE_CHANGED = "WallpaperSettings_ChangeCycle"
  const val RESET_EDITOR = "WallpaperSettings_ResetEditor"
  const val ON_CLICK = "TouchScreen_OnClick"
}
