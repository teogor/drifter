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

package dev.teogor.drifter.codegen.facades

import com.squareup.kotlinpoet.FileSpec
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

interface CodeOutputStreamMaker {

  fun makeFile(
    name: String,
    packageName: String,
    vararg sourceIds: String,
  ): OutputStream
}

fun CodeOutputStreamMaker.writeTo(
  file: FileSpec,
  fileName: String = file.name,
  packageName: String = file.packageName,
) {
  makeFile(
    fileName,
    packageName,
  ).use { out ->
    OutputStreamWriter(out, StandardCharsets.UTF_8).use { writer ->
      file.writeTo(writer)
    }
  }
}
