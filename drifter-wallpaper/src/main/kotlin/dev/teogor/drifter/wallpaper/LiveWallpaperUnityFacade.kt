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
import android.preference.PreferenceManager
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.integration.utilities.MultiTapDetector
import dev.teogor.drifter.wallpaper.UnityWallpaperService.UnityWallpaperEngine
import dev.teogor.drifter.wallpaper.activities.LiveWallpaperCompatibleUnityPlayerActivity

/**
 * Central class for communicating with Unity C# side. All interactions to and from C# side are done here.
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
   * Note: Called from C# code.
   *
   * @return Instance of `LiveWallpaperUnityFacade.PreferenceEditorFacade`.
   */
  val preferencesEditorFacade: LiveWallpaperUnityFacade.PreferenceEditorFacade

  /**
   * Note: Called from C# code.
   *
   * @return Instance of `LiveWallpaperUnityFacade.WallpaperEngineFacade`.
   */
  val wallpaperEngineFacade: LiveWallpaperUnityFacade.WallpaperEngineFacade

  /**
   * Note: Called from C# code.
   *
   * @return Instance of `MultiTapDetector`.
   */
  val multiTapDetector = MultiTapDetector(Point(0, 0))
  /**
   * @return Currently active `UnityWallpaperService.UnityWallpaperEngine`, or null if none is active.
   */
  /**
   * Sets the currently  active `UnityWallpaperService.UnityWallpaperEngine`.
   *
   * @param activeWallpaperEngine Currently active `UnityWallpaperService.UnityWallpaperEngine`.
   */
  /**
   * Currently active `UnityWallpaperService.UnityWallpaperEngine`, or null if none is active.
   */
  var activeWallpaperEngine: UnityWallpaperEngine? = null

  /**
   * @param context Application Context.
   */
  init {
    preferencesEditorFacade = PreferenceEditorFacade()
    wallpaperEngineFacade = WallpaperEngineFacade()
  }

  /**
   * Updates the current Context of the `LiveWallpaperCompatibleUnityPlayerActivity`
   * to allow using soft input.
   * Note: Called from C# code.
   */
  fun updateUnityPlayerActivityContext() {
    LiveWallpaperCompatibleUnityPlayerActivity.updateUnityPlayerActivityContext()
  }

  /**
   * Provides safe access to current `WallpaperService.Engine` methods.
   */
  inner class WallpaperEngineFacade {
    val isVisible: Boolean
      get() = if (activeWallpaperEngine == null) false else activeWallpaperEngine!!.isVisible
    val isPreview: Boolean
      get() = if (activeWallpaperEngine == null) false else activeWallpaperEngine!!.isPreview
  }

  /**
   * Provides safe access to `SharedPreferences` read/write operations.
   */
  inner class PreferenceEditorFacade {
    private val mDefaultSharedPreferences: SharedPreferences
    private var mCurrentPreferencesEditor: SharedPreferences.Editor? = null

    init {
      mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(
        applicationContext,
      )
    }

    /**
     * Commit your preferences changes back from this Editor to the
     * [SharedPreferences] object it is editing.  This atomically
     * performs the requested modifications, replacing whatever is currently
     * in the SharedPreferences.
     */
    @SuppressLint("CommitPrefEdits")
    fun startEditing(): Boolean {
      if (mCurrentPreferencesEditor != null) return false
      mCurrentPreferencesEditor = mDefaultSharedPreferences.edit()
      return true
    }

    /**
     * Commit your preferences changes back from this Editor to the
     * [SharedPreferences] object it is editing.  This atomically
     * performs the requested modifications, replacing whatever is currently
     * in the SharedPreferences.
     */
    fun finishEditing(): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.apply()
      mCurrentPreferencesEditor = null
      return true
    }

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to check.
     * @return Returns true if the preference exists in the preferences, otherwise false.
     */
    fun hasKey(key: String?): Boolean {
      return mDefaultSharedPreferences.contains(key)
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a String.
     * @throws ClassCastException
     */
    fun getString(key: String?, defValue: String?): String? {
      return mDefaultSharedPreferences.getString(key, defValue)
    }
    /**
     * Retrieve a set of String values from the preferences.
     *
     *
     * Note that you *must not* modify the set instance returned
     * by this call.  The consistency of the stored data is not guaranteed
     * if you do, nor is your ability to modify the instance at all.
     *
     * @param key The name of the preference to retrieve.
     * @param defValues Values to return if this preference does not exist.
     *
     * @return Returns the preference values if they exist, or defValues.
     * Throws ClassCastException if there is a preference with this name
     * that is not a Set.
     *
     * @throws ClassCastException
     */
    // @Nullable
    // Set<String> getStringSet(String key, @Nullable Set<String> defValues);
    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    fun getInt(key: String?, defValue: Int): Int {
      return mDefaultSharedPreferences.getInt(key, defValue)
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a long.
     * @throws ClassCastException
     */
    fun getLong(key: String?, defValue: Long): Long {
      return mDefaultSharedPreferences.getLong(key, defValue)
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a float.
     * @throws ClassCastException
     */
    fun getFloat(key: String?, defValue: Float): Float {
      return mDefaultSharedPreferences.getFloat(key, defValue)
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * a boolean.
     * @throws ClassCastException
     */
    fun getBoolean(key: String?, defValue: Boolean): Boolean {
      return mDefaultSharedPreferences.getBoolean(key, defValue)
    }

    /**
     * Set a String value in the preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun putString(key: String?, value: String?): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.putString(key, value)
      return true
    }
    /**
     * Set a set of String values in the preferences.
     *
     * @param key The name of the preference to modify.
     * @param values The set of new values for the preference.  Passing `null`
     * for this argument is equivalent to calling [.remove] with
     * this key.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    // Editor putStringSet(String key, @Nullable Set<String> values);
    /**
     * Set an int value in the preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun putInt(key: String?, value: Int): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.putInt(key, value)
      return true
    }

    /**
     * Set a long value in the preferences
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun putLong(key: String?, value: Long): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.putLong(key, value)
      return true
    }

    /**
     * Set a float value in the preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun putFloat(key: String?, value: Float): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.putFloat(key, value)
      return true
    }

    /**
     * Set a boolean value in the preferences.
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun putBoolean(key: String?, value: Boolean): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.putBoolean(key, value)
      return true
    }

    /**
     * Remove value from the preferences.
     *
     * @param key The name of the preference to remove.
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun remove(key: String?): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.remove(key)
      return true
    }

    /**
     * Remove all values from the preferences.
     *
     * @return true if editing has started and operation succeeded, false otherwise.
     */
    fun clear(): Boolean {
      if (mCurrentPreferencesEditor == null) return false
      mCurrentPreferencesEditor!!.clear()
      return true
    }
  }

  companion object {
    /**
     * Note: Called from C# code.
     *
     * @return Instance of `UnityEventsProxy`.
     */
    val eventsProxy = UnityEventsProxy()

    private var liveWallpaperUnityFacadeInstance: LiveWallpaperUnityFacade? =
      null

    val instance: LiveWallpaperUnityFacade? by lazy(
      LazyThreadSafetyMode.SYNCHRONIZED,
    ) {
      if (liveWallpaperUnityFacadeInstance == null) {
        UnityPlayerInstanceManager.instance.unityPlayerWrapperInstance
          ?.let { unityPlayerWrapper ->
            val applicationContext = unityPlayerWrapper.applicationContext
            liveWallpaperUnityFacadeInstance =
              LiveWallpaperUnityFacade(applicationContext)
          }
      }
      liveWallpaperUnityFacadeInstance
    }
  }
}
