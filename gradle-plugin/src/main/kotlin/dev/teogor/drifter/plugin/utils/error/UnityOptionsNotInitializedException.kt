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

package dev.teogor.drifter.plugin.utils.error

import dev.teogor.drifter.plugin.RefreshUnityAssetsTask

/**
 * Exception thrown when attempting to use the task before initializing UnityOptions.
 *
 * @see [RefreshUnityAssetsTask.checkInitialized]
 */
class UnityOptionsNotInitializedException : RuntimeException(
  """
  |UnityOptions is not initialized. Please call setUnityOptions() before using this task.
  |
  |To report this problem, file an issue on GitHub: https://github.com/teogor/drifter/issues
  """.trimMargin(),
)
