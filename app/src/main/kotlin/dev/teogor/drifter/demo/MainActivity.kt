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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import dev.teogor.ceres.core.common.utils.OnLifecycleEvent
import dev.teogor.drifter.compose.UvpComposable
import dev.teogor.drifter.demo.ui.theme.UnityViewTheme
import dev.teogor.drifter.wallpaper.LiveWallpaperUtility

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      UnityViewTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.primary,
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 30.dp, vertical = 100.dp)
              .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(20.dp),
              ),
            contentAlignment = Alignment.Center,
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              val context = LocalContext.current

              val controller = AquariumMessageSender()
              val storage = AquariumStorage()

              UvpComposable(
                modifier = Modifier
                  .fillMaxWidth(.5f)
                  .fillMaxHeight(.5f)
                  .backgroundAndClip(
                    color = Color.Green,
                    shape = RoundedCornerShape(20.dp),
                  ),
                onCreated = {
                  controller.apply {
                    setEditorMode(true)
                    animateToWaterColor(storage.waterColor, false)
                    cycleOption(storage.cycleOption)
                  }
                },
              )

              UnityColorPicker(
                controller = controller,
                storage = storage,
              )

              Button(
                onClick = {
                  LiveWallpaperUtility.openWallpaperPreview(context)
                },
              ) {
                Text(
                  text = "Launch WLP Settings",
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun UnityColorPicker(
  controller: AquariumMessageSender,
  storage: AquariumStorage,
) {
  val colors = listOf(
    Color(0xFFB19CD9), // Lavender
    Color(0xFF6FA3EF), // Sky Blue
    Color(0xFFC4E17F), // Light Green
    Color(0xFFFFBCBC), // Pastel Pink
    Color(0xFFFFD700), // Pastel Yellow
    Color(0xFFFFB6C1), // Pastel Pink
    Color(0xFF98FB98), // Pale Green
    Color(0xFFFFA07A), // Light Salmon
    Color(0xFFA9A9A9), // Dark Gray
    Color(0xFF00CED1), // Dark Turquoise
    Color(0xFFDDA0DD), // Plum
    Color(0xFF87CEEB), // Sky Blue
    Color(0xFFFFA07A), // Light Salmon
    Color(0xFF00FF7F), // Spring Green
    Color(0xFFFFE4E1), // Misty Rose
    Color(0xFF87CEEB), // Sky Blue
    Color(0xFF32CD32), // Lime Green
    Color(0xFFE9967A), // Dark Salmon
    Color(0xFFFF69B4), // Hot Pink
    Color(0xFF00BFFF), // Deep Sky Blue
  )

  var selectedColor by remember {
    val color = storage.waterColor
    mutableStateOf(
      if (color == Color.Unspecified || !colors.contains(color)) {
        colors[0]
      } else {
        color
      },
    )
  }

  LaunchedEffect(selectedColor) {
    controller.animateToWaterColor(selectedColor, true)
  }

  OnLifecycleEvent { _, event ->
    when (event) {
      Lifecycle.Event.ON_RESUME -> {
        controller.setEditorMode(true)
        controller.animateToWaterColor(selectedColor, true)
      }

      Lifecycle.Event.ON_PAUSE -> {
        controller.setEditorMode(false)
      }

      else -> {}
    }
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    for (color in colors) {
      ColorCircle(
        color = color,
        isSelected = color == selectedColor,
        onColorSelected = {
          selectedColor = color
        },
      )
    }
  }

  Button(
    onClick = {
      storage.waterColor = selectedColor
    },
    modifier = Modifier
      .padding(top = 4.dp),
  ) {
    Text("Save")
  }
}

@Composable
fun ColorCircle(
  color: Color,
  isSelected: Boolean,
  onColorSelected: () -> Unit,
) {
  Box(
    modifier = Modifier
      .size(50.dp)
      .padding(if (isSelected) 6.dp else 2.dp)
      .border(
        width = 2.dp,
        color = MaterialTheme.colorScheme.outline,
        shape = CircleShape,
      )
      .backgroundAndClip(color, CircleShape)
      .clickable { onColorSelected() },
  ) {
  }
}

fun Modifier.backgroundAndClip(
  color: Color,
  shape: Shape,
): Modifier = this
  .background(color, shape)
  .clip(shape)
