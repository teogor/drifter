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

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.service.wallpaper.WallpaperService
import androidx.preference.PreferenceManager
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.utilities.MultiTapDetector
import dev.teogor.drifter.wallpaper.UnityWallpaperService.UnityWallpaperEngine
import dev.teogor.drifter.wallpaper.activities.LiveWallpaperCompatibleUnityPlayerActivity

/**
 * Central class for interacting with Unity from the wallpaper service.
 *
 * This class provides access to various functionalities required for communication
 * with the Unity C# side of the application, including managing preferences and
 * interacting with the wallpaper engine.
 *
 * @property applicationContext The application context used for various operations.
 */
class LiveWallpaperUnityFacade private constructor(
  /**
   * Note: Called from C# code.
   *
   * @return Application `Context`.
   */
  val applicationContext: Context,
) {

  /**
   * Provides access to shared preferences for editing and retrieval.
   *
   * Note: Called from C# code.
   *
   * @return Instance of [PreferenceEditorFacade].
   */
  val preferencesEditorFacade: PreferenceEditorFacade = PreferenceEditorFacade()

  /**
   * Provides access to the current wallpaper engine state.
   *
   * Note: Called from C# code.
   *
   * @return Instance of [WallpaperEngineFacade].
   */
  val wallpaperEngineFacade: WallpaperEngineFacade = WallpaperEngineFacade()

  /**
   * Detects multi-tap gestures within the wallpaper area.
   *
   * Note: Called from C# code.
   *
   * @return Instance of [MultiTapDetector].
   */
  val multiTapDetector = MultiTapDetector(Point(0, 0))

  /**
   * The currently active wallpaper engine, or `null` if no engine is active.
   */
  var activeWallpaperEngine: UnityWallpaperEngine? = null

  /**
   * Updates the context of [LiveWallpaperCompatibleUnityPlayerActivity] to allow
   * using soft input.
   *
   * Note: Called from C# code.
   */
  fun updateUnityPlayerActivityContext() {
    LiveWallpaperCompatibleUnityPlayerActivity.updateUnityPlayerActivityContext()
  }

  /**
   * Provides safe access to current [WallpaperService.Engine] methods.
   */
  inner class WallpaperEngineFacade {
    /**
     * Indicates whether the wallpaper engine is currently visible.
     *
     * @return `true` if the engine is visible, `false` otherwise.
     */
    val isVisible: Boolean
      get() = activeWallpaperEngine?.isVisible ?: false

    /**
     * Indicates whether the wallpaper engine is in preview mode.
     *
     * @return `true` if the engine is in preview mode, `false` otherwise.
     */
    val isPreview: Boolean
      get() = activeWallpaperEngine?.isPreview ?: false
  }

  /**
   * Provides safe access to shared preferences read/write operations.
   */
  inner class PreferenceEditorFacade {
    private val mDefaultSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
      applicationContext,
    )
    private var mCurrentPreferencesEditor: SharedPreferences.Editor? = null

    /**
     * Begins editing the shared preferences. Changes are not committed until
     * [finishEditing] is called.
     *
     * @return `true` if editing has started successfully, `false` otherwise.
     */
    @SuppressLint("CommitPrefEdits")
    fun startEditing(): Boolean {
      if (mCurrentPreferencesEditor != null) return false
      mCurrentPreferencesEditor = mDefaultSharedPreferences.edit()
      return true
    }

    /**
     * Commits changes made during editing to the shared preferences.
     *
     * @return `true` if changes were committed successfully, `false` otherwise.
     */
    fun finishEditing(): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.apply()
      mCurrentPreferencesEditor = null
      return true
    }

    /**
     * Checks if the shared preferences contain a specific key.
     *
     * @param key The name of the preference to check.
     * @return `true` if the preference exists, `false` otherwise.
     */
    fun hasKey(key: String?): Boolean {
      return mDefaultSharedPreferences.contains(key)
    }

    /**
     * Retrieves a String value from the shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if the preference does not exist.
     * @return The preference value if it exists, otherwise `defValue`.
     * @throws ClassCastException If there is a preference with this name that is not a String.
     */
    fun getString(key: String?, defValue: String?): String? {
      return mDefaultSharedPreferences.getString(key, defValue)
    }

    /**
     * Retrieves a set of String values from the shared preferences.
     *
     * Note that the returned set should not be modified.
     *
     * @param key The name of the preference to retrieve.
     * @param defValues Values to return if this preference does not exist.
     * @return The set of preference values if they exist, otherwise `defValues`.
     * @throws ClassCastException If there is a preference with this name that is not a Set.
     */
    fun getStringSet(key: String, defValues: Set<String>? = null): Set<String>? {
      return mDefaultSharedPreferences.getStringSet(key, defValues)
    }

    /**
     * Retrieves an int value from the shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if the preference does not exist.
     * @return The preference value if it exists, otherwise `defValue`.
     * @throws ClassCastException If there is a preference with this name that is not an int.
     */
    fun getInt(key: String?, defValue: Int): Int {
      return mDefaultSharedPreferences.getInt(key, defValue)
    }

    /**
     * Retrieves a long value from the shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if the preference does not exist.
     * @return The preference value if it exists, otherwise `defValue`.
     * @throws ClassCastException If there is a preference with this name that is not a long.
     */
    fun getLong(key: String?, defValue: Long): Long {
      return mDefaultSharedPreferences.getLong(key, defValue)
    }

    /**
     * Retrieves a float value from the shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if the preference does not exist.
     * @return The preference value if it exists, otherwise `defValue`.
     * @throws ClassCastException If there is a preference with this name that is not a float.
     */
    fun getFloat(key: String?, defValue: Float): Float {
      return mDefaultSharedPreferences.getFloat(key, defValue)
    }

    /**
     * Retrieves a boolean value from the shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @param defValue Value to return if the preference does not exist.
     * @return The preference value if it exists, otherwise `defValue`.
     * @throws ClassCastException If there is a preference with this name that is not a boolean.
     */
    fun getBoolean(key: String?, defValue: Boolean): Boolean {
      return mDefaultSharedPreferences.getBoolean(key, defValue)
    }

    /**
     * Sets a String value in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return `true` if the value was set successfully, `false` otherwise.
     */
    fun putString(key: String?, value: String?): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.putString(key, value)
      return true
    }

    /**
     * Sets a set of String values in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param values The set of new values for the preference. Passing `null` is equivalent to calling
     * [.remove] with this key.
     * @return `true` if the values were set successfully, `false` otherwise.
     */
    fun putStringSet(key: String, values: Set<String>?): Boolean {
      mCurrentPreferencesEditor?.putStringSet(key, values)
      return true
    }

    /**
     * Sets an int value in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return `true` if the value was set successfully, `false` otherwise.
     */
    fun putInt(key: String?, value: Int): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.putInt(key, value)
      return true
    }

    /**
     * Sets a long value in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return `true` if the value was set successfully, `false` otherwise.
     */
    fun putLong(key: String?, value: Long): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.putLong(key, value)
      return true
    }

    /**
     * Sets a float value in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return `true` if the value was set successfully, `false` otherwise.
     */
    fun putFloat(key: String?, value: Float): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.putFloat(key, value)
      return true
    }

    /**
     * Sets a boolean value in the shared preferences.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     * @return `true` if the value was set successfully, `false` otherwise.
     */
    fun putBoolean(key: String?, value: Boolean): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.putBoolean(key, value)
      return true
    }

    /**
     * Removes a preference from the shared preferences.
     *
     * @param key The name of the preference to remove.
     * @return `true` if the value was removed successfully, `false` otherwise.
     */
    fun remove(key: String?): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.remove(key)
      return true
    }

    /**
     * Clears all preferences from the shared preferences.
     *
     * @return `true` if all values were removed successfully, `false` otherwise.
     */
    fun clear(): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor?.clear()
      return true
    }
  }

  companion object {
    /**
     * Provides access to Unity events.
     *
     * Note: Called from C# code.
     *
     * @return Instance of [UnityEventsProxy].
     */
    val eventsProxy = UnityEventsProxy()

    private var liveWallpaperUnityFacadeInstance: LiveWallpaperUnityFacade? = null

    /**
     * Provides the singleton instance of [LiveWallpaperUnityFacade].
     *
     * Initializes the instance if it has not been created yet using
     * [UnityPlayerInstanceManager].
     *
     * @return Singleton instance of [LiveWallpaperUnityFacade].
     */
    val instance: LiveWallpaperUnityFacade? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
      liveWallpaperUnityFacadeInstance ?: UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance?.let { unityPlayerWrapper ->
        LiveWallpaperUnityFacade(unityPlayerWrapper.applicationContext).also {
          liveWallpaperUnityFacadeInstance = it
        }
      }
    }
  }
}
