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

package dev.teogor.drifter.demo.unity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.unity3d.player.UnityPlayer
import org.json.JSONObject

class AndroidHandler {

  fun setEditorMode(isEditorMode: Boolean) {
    UnityPlayer.UnitySendMessage(
      "AndroidHandler",
      "Receiver_SetEditorMode",
      JSONObject().apply {
        put("isEditorMode", isEditorMode)
      }.toString(),
    )
  }

  fun setEditorColour(
    colour: Color,
    animated: Boolean = true,
  ) {
    UnityPlayer.UnitySendMessage(
      "AndroidHandler",
      "Receiver_SetEditorColour",
      JSONObject().apply {
        put("color", colour.toArgb().toUInt())
        put("animated", animated)
      }.toString(),
    )
  }
}
