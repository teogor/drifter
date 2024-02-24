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

@file:Suppress("SameParameterValue")

package dev.teogor.drifter.ksp.processors

import dev.teogor.drifter.codegen.model.CodeGenConfig

class ConfigParser(
  private val options: Map<String, String>,
) {

  companion object {
    private const val PREFIX = "drifter"

    // Configs
    private const val ADD_DOCUMENTATION = "$PREFIX.addDocumentation"
    private const val GENERATE_OPERATIONS = "$PREFIX.generateOperations"
    private const val GENERATED_PACKAGE_NAME = "$PREFIX.generatedPackageName"
  }

  fun parse(): CodeGenConfig {
    val addDocumentation = parseBoolean(ADD_DOCUMENTATION) ?: true
    val generateOperations = parseBoolean(GENERATE_OPERATIONS) ?: true
    val generatedPackageName = options[GENERATED_PACKAGE_NAME]?.trim()?.removeSuffix(".")

    return CodeGenConfig(
      addDocumentation = addDocumentation,
      generateOperations = generateOperations,
      generatedPackageName = generatedPackageName,
    )
  }

  private fun parseBoolean(key: String): Boolean? {
    return options[key]?.runCatching {
      toBooleanStrict()
    }?.getOrElse {
      throw WrongConfigurationSetup("$key must be a boolean value!", cause = it)
    }
  }
}

class WrongConfigurationSetup(message: String, cause: Throwable? = null) :
  RuntimeException(message, cause)
