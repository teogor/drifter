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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.teogor.drifter.core.UnityPlayerPrefs
import dev.teogor.drifter.core.UnityPrefAccess
import dev.teogor.drifter.core.createSerializable
import dev.teogor.drifter.demo.models.CycleOption

class AquariumStorage : UnityPlayerPrefs() {
  private val colorConverter = createSerializable(
    encodeToString = { color -> color.toArgb().toString() },
    decodeFromString = { string -> Color(string.toInt()) },
  )

  @OptIn(UnityPrefAccess::class)
  var waterColor: Color = Color(0xFF6FA3EF)
    get() {
      field = get(
        key = AquariumKeyConstants.WATER_COLOR,
        defaultValue = field,
        converter = colorConverter,
      )
      return field
    }
    set(value) {
      field = set(
        key = AquariumKeyConstants.WATER_COLOR,
        value = value,
        converter = colorConverter,
      )
    }

  @OptIn(UnityPrefAccess::class)
  var cycleOption: CycleOption = CycleOption.Day
    get() {
      val cycleOptionContent = get(
        key = AquariumKeyConstants.CYCLE_OPTION,
        defaultValue = field.ordinal,
      )
      field = CycleOption.entries[cycleOptionContent]
      return field
    }
    set(value) {
      field = value
      set(
        key = AquariumKeyConstants.CYCLE_OPTION,
        value = value.ordinal,
      )
    }
}
