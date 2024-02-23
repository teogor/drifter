package dev.teogor.drifter.demo.unity.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONObject

data class UnityActionParams(
  val isEditorMode: Boolean? = null,
  val waterColor: Color? = null,
  val animated: Boolean? = null,
  val cycleOption: CycleOption? = null
)

fun UnityActionParams.toJsonObject(): JSONObject {
    val json = JSONObject()
    isEditorMode?.let { json.put("isEditorMode", it) }
    waterColor?.let { json.put("waterColor", it.toArgb()) }
    animated?.let { json.put("animated", it) }
    cycleOption?.let { json.put("cycleOption", it) }
    return json
}
