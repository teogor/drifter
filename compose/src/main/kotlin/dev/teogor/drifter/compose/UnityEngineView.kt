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

package dev.teogor.drifter.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import dev.teogor.ceres.core.common.utils.OnLifecycleEvent
import dev.teogor.drifter.compose.components.UnityPlayerView

@Deprecated(
  "Use UnityEngineView directly instead. This function will be removed in a future version.",
  ReplaceWith("UnityEngineView(modifier = modifier, onUnityEngineCreated = onCreated)"),
)
@Composable
fun UvpComposable(
  modifier: Modifier = Modifier,
  onCreated: () -> Unit,
) {
  UnityEngineView(modifier, onCreated)
}

/**
 * Composable function that displays the Unity Engine instance within your Compose UI.
 *
 * @param modifier Modifier to apply to the Unity Engine view.
 * @param onUnityEngineCreated Callback invoked when the Unity Engine is created and ready.
 */
@Composable
fun UnityEngineView(
  modifier: Modifier = Modifier,
  onUnityEngineCreated: () -> Unit,
) {
  var unityEngineView by remember {
    mutableStateOf<UnityPlayerView?>(null)
  }

  OnLifecycleEvent { _, event ->
    when (event) {
      Lifecycle.Event.ON_START -> {
        unityEngineView?.onStart()
      }

      Lifecycle.Event.ON_RESUME -> {
        unityEngineView?.onResume()
      }

      Lifecycle.Event.ON_PAUSE -> {
        unityEngineView?.onPause()
      }

      Lifecycle.Event.ON_STOP -> {
        unityEngineView?.onStop()
      }

      Lifecycle.Event.ON_DESTROY -> {
        unityEngineView?.onDestroy()
      }

      else -> {}
    }
  }

  AndroidView(
    modifier = modifier,
    factory = { context ->
      UnityPlayerView(context).apply {
        loadPlayer(onUnityEngineCreated = onUnityEngineCreated)
        unityEngineView = this
      }
    },
  )
}

/**
 * Composable function for creating a layout with a Unity Engine view and additional content.
 *
 * @param modifier Modifier to apply to the overall layout.
 * @param unityEngineModifier Modifier to apply specifically to the Unity Engine view.
 * @param onUnityEngineCreated Optional callback invoked when the Unity Engine is created
 * and ready.
 * @param content The composable content to display alongside the Unity Engine.
 */
@Composable
fun UnityEngineScaffold(
  modifier: Modifier = Modifier,
  unityEngineModifier: Modifier = Modifier,
  onUnityEngineCreated: () -> Unit = {},
  content: @Composable BoxScope.() -> Unit,
) {
  Box(modifier) {
    UnityEngineView(
      modifier = unityEngineModifier,
      onUnityEngineCreated = onUnityEngineCreated,
    )

    content()
  }
}
