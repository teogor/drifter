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

package com.unity3d.player.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.unity3d.player.UnityPlayer

@Suppress("DEPRECATION")
class PhoneStateChangeListener(
  private val context: Context,
  private val unityPlayer: UnityPlayer,
) {
  private val telephonyManager: TelephonyManager = context.getSystemService(
    Context.TELEPHONY_SERVICE,
  ) as TelephonyManager

  fun registerPhoneListenerNone() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      registerTelephonyCallback()
    } else {
      val phoneStateListener = getPhoneListener()
      telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
  }

  fun registerPhoneListenerCallState() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      registerTelephonyCallback()
    } else {
      val phoneStateListener = getPhoneListener()
      telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.S)
  private fun registerTelephonyCallback() {
    val telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
      override fun onCallStateChanged(state: Int) {
        handleCallStateChanged(state)
      }
    }
    telephonyManager.registerTelephonyCallback(
      context.mainExecutor,
      telephonyCallback,
    )
  }

  @TargetApi(Build.VERSION_CODES.R)
  private fun getPhoneListener() = object : PhoneStateListener() {
    @Deprecated("Deprecated in Java")
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
      handleCallStateChanged(state)
    }
  }

  private fun handleCallStateChanged(state: Int) {
    // Handle call state changes here
    unityPlayer.nativeMuteMasterAudio(state == TelephonyManager.CALL_STATE_OFFHOOK)
  }
}
