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

package dev.teogor.drifter.demo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.teogor.drifter.DrifterEncoder
import dev.teogor.drifter.DrifterMappingKey
import dev.teogor.drifter.DrifterModule
import dev.teogor.drifter.DrifterUnityMethod
import dev.teogor.drifter.demo.models.CycleOption

@DrifterModule(
  name = "Aquarium",
  receiver = "BridgeController",
  methods = AquariumMethods::class,
)
data class AquariumModule(
  @DrifterMappingKey(
    exposedMethod = "SetEditorMode",
  )
  val isEditorMode: Boolean? = null,
  val waterColor: Color? = null,
  val animated: Boolean? = null,
  val cycleOption: CycleOption,
  val statusBarColor: Color,
  val statusBarIsVisible: Boolean,
  val statusBarOpacity: Float,
  val statusBarHeight: Int,
)

interface AquariumMethods {
  @DrifterUnityMethod(
    name = "animateToWaterColor",
    parameters = [
      "waterColor",
      "animated",
    ],
  )
  fun animateToWaterColor()
}

@DrifterEncoder
fun Color.encodeFromColor() = toArgb()
