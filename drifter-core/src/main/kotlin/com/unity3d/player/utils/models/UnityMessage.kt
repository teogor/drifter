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

package com.unity3d.player.utils.models

import com.unity3d.player.UnityPlayer

enum class UnityMessage(val value: Int) {
  PAUSE(0),
  RESUME(1),
  QUIT(2),
  SURFACE_LOST(3),
  SURFACE_ACQUIRED(UnityPlayer.ANR_TIMEOUT_SECONDS),
  FOCUS_LOST(5),
  FOCUS_GAINED(6),
  NEXT_FRAME(7),
  URL_ACTIVATED(8),
  ORIENTATION_ANGLE_CHANGE(9),
}
