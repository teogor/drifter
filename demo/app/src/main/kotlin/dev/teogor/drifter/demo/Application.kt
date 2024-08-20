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

package dev.teogor.drifter.demo

import android.app.Application
import dev.teogor.ceres.core.register.RegistryStartup
import dev.teogor.drifter.unity.withUnity202237f1Factory

/**
 * Android Application class for Ceres application.
 *
 * This class extends the Android [Application] class.
 */
class Application : Application() {

  override fun onCreate() {
    super.onCreate()

    RegistryStartup.provides(withUnity202237f1Factory())
  }
}
