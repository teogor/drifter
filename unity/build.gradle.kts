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

import dev.teogor.winds.api.ArtifactIdFormat
import dev.teogor.winds.ktx.createVersion

plugins {
  alias(libs.plugins.teogor.winds)
}

winds {
  moduleMetadata {
    artifactDescriptor {
      name = "Unity"
      version = createVersion(1, 0, 0) {
        alphaRelease(1)
      }
      artifactIdFormat = ArtifactIdFormat.FULL
    }

    publishing {
      enabled = false
    }
  }
}
