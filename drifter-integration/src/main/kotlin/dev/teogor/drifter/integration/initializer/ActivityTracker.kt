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

package dev.teogor.drifter.integration.initializer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

class ActivityTracker(
  applicationContext: Context,
) {
  init {
    ActivityContextProvider.applicationContext = applicationContext

    registerActivityLifecycleCallbacks(applicationContext)
  }

  private fun registerActivityLifecycleCallbacks(applicationContext: Context) {
    if (applicationContext is Application) {
      applicationContext.registerActivityLifecycleCallbacks(
        object :
          Application.ActivityLifecycleCallbacks {
          override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            println("ActivityTracker :: onActivityResumed(${activity.javaClass.simpleName})")
            ActivityContextProvider.currentActivity = activity
          }

          override fun onActivityStarted(activity: Activity) {
          }

          override fun onActivityResumed(activity: Activity) {
            println("ActivityTracker :: onActivityResumed(${activity.javaClass.simpleName})")
            ActivityContextProvider.currentActivity = activity
          }

          override fun onActivityPaused(activity: Activity) {
          }

          override fun onActivityStopped(activity: Activity) {
          }

          override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
          }

          override fun onActivityDestroyed(activity: Activity) {
          }
        },
      )
    }
  }
}
