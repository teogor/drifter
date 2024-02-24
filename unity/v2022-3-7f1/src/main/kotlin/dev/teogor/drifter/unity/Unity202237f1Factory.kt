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

package dev.teogor.drifter.unity

import android.content.ContextWrapper
import com.unity3d.player.UnityPlayer
import dev.teogor.drifter.unity.common.IUnityPlayer
import dev.teogor.drifter.unity.common.LocalUnityEngine
import dev.teogor.drifter.unity.common.UnityEngineFactory
import dev.teogor.drifter.unity.common.UnityOptions
import dev.teogor.drifter.unity.common.configureOptions

class Unity202237f1Factory : UnityEngineFactory {
  override val version = "2022.3.7f1"

  override fun createUnityPlayer(
    contextWrapper: ContextWrapper,
    options: UnityOptions,
  ): IUnityPlayer = UnityPlayer(contextWrapper)
    .configureOptions(
      options,
      contextWrapper,
    ) {
      setDetachFromActivity(true)
    }

  override fun sendMessage(gameObject: String, methodName: String, funcParam: String) {
    UnityPlayer.UnitySendMessage(
      gameObject,
      methodName,
      funcParam,
    )
  }
}

fun withUnity202237f1Factory() = LocalUnityEngine provide Unity202237f1Factory()
