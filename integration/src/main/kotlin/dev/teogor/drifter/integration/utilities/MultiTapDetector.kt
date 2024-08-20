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

package dev.teogor.drifter.integration.utilities

import android.graphics.Point
import android.os.SystemClock
import android.view.MotionEvent
import dev.teogor.drifter.integration.utilities.DebugLog.v
import java.util.LinkedList
import kotlin.math.sqrt

/**
 * Detects quick sequences of touch taps and notifies listeners about them.
 */
class MultiTapDetector(
  @JvmField var screenSize: Point,
) {
  private val mMultiTapDetectedListeners: MutableList<IMultiTapDetectedListener> = LinkedList()
  private var mScreenSize = Point()
  private var mNumberOfTaps = 3
  private var mMaxTimeBetweenTaps: Long = 250
  private var mTapZoneRadiusRelative = 0.15f
  private var mTapZoneRadiusAbsolute = 0f
  private var mLastTapTime: Long = 0
  private var mLastTapPositionX = 0f
  private var mLastTapPositionY = 0f
  private var mCurrentTapCount = 0

  fun onTouchEvent(motionEvent: MotionEvent) {
    // We are only interested in the tap event
    if (motionEvent.action != MotionEvent.ACTION_DOWN) return
    val currentTime = SystemClock.uptimeMillis()
    val timeDeltaBetweenTaps = currentTime - mLastTapTime
    val tapPositionX = motionEvent.x
    val tapPositionY = motionEvent.y
    if (mCurrentTapCount > 0) {
      // Reset if taps are too far apart in time or position
      if (timeDeltaBetweenTaps > mMaxTimeBetweenTaps) {
        mCurrentTapCount = 0
      } else {
        val distanceToLastTapPosition = distanceBetweenPoints(
          mLastTapPositionX,
          mLastTapPositionY,
          tapPositionX,
          tapPositionY,
        )
        if (distanceToLastTapPosition > mTapZoneRadiusAbsolute) {
          mCurrentTapCount = 0
        }
      }
    }
    mLastTapTime = currentTime
    mLastTapPositionX = tapPositionX
    mLastTapPositionY = tapPositionY
    mCurrentTapCount++
    if (mCurrentTapCount >= mNumberOfTaps) {
      mCurrentTapCount = 0
      notifyListeners()
      v("MultiTapDetector.notifyListeners();")
    }
  }

  var numberOfTaps: Int
    get() = mNumberOfTaps
    set(numberOfTaps) {
      require(numberOfTaps >= 1) { "numberOfTaps must be >= 1" }
      mNumberOfTaps = numberOfTaps
    }
  var maxTimeBetweenTaps: Long
    get() = mMaxTimeBetweenTaps
    set(maxTimeBetweenTaps) {
      require(maxTimeBetweenTaps > 0) { "maxTimeBetweenTaps must be positive" }
      mMaxTimeBetweenTaps = maxTimeBetweenTaps
    }
  var tapZoneRadiusRelative: Float
    get() = mTapZoneRadiusRelative
    set(tapZoneRadiusRelative) {
      require(!(tapZoneRadiusRelative <= 0)) { "tapZoneRadiusRelative must be positive" }
      require(!(tapZoneRadiusRelative > 1f || tapZoneRadiusRelative < 0.01f)) {
        "tapZoneRadiusRelative must be in range [0.01, 1]"
      }
      mTapZoneRadiusRelative = tapZoneRadiusRelative
      updateTapRadius()
    }

  @Synchronized
  fun registerMultiTapDetectedListener(listener: IMultiTapDetectedListener?) {
    requireNotNull(listener) { "listener == null" }
    mMultiTapDetectedListeners.add(listener)
  }

  @Synchronized
  fun unregisterMultiTapDetectedListener(listener: IMultiTapDetectedListener?) {
    requireNotNull(listener) { "listener == null" }
    mMultiTapDetectedListeners.remove(listener)
  }

  private fun updateTapRadius() {
    mTapZoneRadiusAbsolute = (mScreenSize.x + mScreenSize.y) * 0.5f * mTapZoneRadiusRelative
  }

  private fun notifyListeners() {
    for (listener in mMultiTapDetectedListeners) {
      listener.onMultiTapDetected(mLastTapPositionX, mLastTapPositionY)
    }
  }

  // todo fun ?? use-case
  fun interface IMultiTapDetectedListener {
    fun onMultiTapDetected(finalTapPositionX: Float, finalTapPositionY: Float)
  }

  companion object {
    private fun distanceBetweenPoints(
      point1x: Float,
      point1y: Float,
      point2x: Float,
      point2y: Float,
    ): Float {
      val xDiff = (point1x - point2x).toDouble()
      val yDiff = (point1y - point2y).toDouble()
      return sqrt(xDiff * xDiff + yDiff * yDiff).toFloat()
    }
  }
}
