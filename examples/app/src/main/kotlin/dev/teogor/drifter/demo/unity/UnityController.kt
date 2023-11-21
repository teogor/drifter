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
import dev.teogor.drifter.demo.unity.models.CycleOption
import dev.teogor.drifter.demo.unity.models.UnityActionParams
import dev.teogor.drifter.demo.unity.models.UnityActions
import dev.teogor.drifter.demo.unity.models.toJsonObject
import dev.teogor.drifter.integration.core.UnityControllerBase

class UnityController : UnityControllerBase(
  receiver = "BridgeController",
) {

  fun setEditorMode(isEditorMode: Boolean) {
    val params = UnityActionParams(isEditorMode = isEditorMode)
    invokeAction(
      UnityActions.SetEditorMode,
      params.toJsonObject(),
    )
  }

  fun setEditorColor(waterColor: Color, animated: Boolean = true) {
    val params = UnityActionParams(waterColor = waterColor, animated = animated)
    invokeAction(
      UnityActions.SetWaterColor,
      params.toJsonObject(),
    )
  }

  fun setEditorCycleOption(cycleOption: CycleOption) {
    val params = UnityActionParams(cycleOption = cycleOption)
    invokeAction(
      UnityActions.SetCycleOption,
      params.toJsonObject(),
    )
  }

}
