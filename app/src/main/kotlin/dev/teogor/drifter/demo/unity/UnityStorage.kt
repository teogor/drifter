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
import dev.teogor.drifter.demo.unity.models.CycleOption
import dev.teogor.drifter.demo.unity.models.StorageKeys
import dev.teogor.drifter.integration.common.TypeConverter
import dev.teogor.drifter.integration.core.UnityStorageBase

class UnityStorage : UnityStorageBase() {

  private val colorConverter = TypeConverter(
    toString = { color -> color.toArgb().toString() },
    fromString = { string -> Color(string.toInt()) },
  )

  var waterColor: Color = Color(0xFF6FA3EF)
    get() {
      field = getStorageElement(
        key = StorageKeys.WaterColor,
        defaultValue = field,
        converter = colorConverter,
      )
      return field
    }
    set(value) {
      println("Write Color ::")
      field = writeElement(
        key = StorageKeys.WaterColor,
        content = value,
        converter = colorConverter,
      )
    }

  var cycleOption: CycleOption = CycleOption.Day
    get() {
      val cycleOptionContent = getStorageElement(
        key = StorageKeys.CycleOption,
        defaultValue = field.ordinal,
      )
      field = CycleOption.entries[cycleOptionContent]
      return field
    }
    set(value) {
      field = value
      writeElement(
        key = StorageKeys.CycleOption,
        content = value.ordinal,
      )
    }
}
