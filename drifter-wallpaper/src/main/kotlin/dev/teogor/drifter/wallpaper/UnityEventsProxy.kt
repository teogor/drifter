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

package dev.teogor.drifter.wallpaper

import java.util.concurrent.LinkedBlockingDeque

/**
 * Notifies about Live Wallpaper events.
 * Compares the event data with previous event data and only sends the event if data has changed.
 *
 *
 * Note: Currently, the only listener is on the Unity C# side.
 */
class UnityEventsProxy : ILiveWallpaperEventsListener {
  /**
   * Listeners to the `WallpaperService.Engine` callbacks.
   */
  private val mLiveWallpaperEventsListeners: MutableList<ILiveWallpaperEventsListener> =
    ArrayList()
  private val mVisibilityChangedEventDispatcher = VisibilityChangedEventDispatcher()
  private val mOffsetsChangedEventDispatcher = OffsetsChangedEventDispatcher()
  private val mIsPreviewChangedEventDispatcher = IsPreviewChangedEventDispatcher()
  private val mDesiredSizeChangedEventDispatcher = DesiredSizeChangedEventDispatcher()
  private val mPreferenceChangedEventDispatcher = PreferenceChangedEventDispatcher()
  private val mPreferenceActivityTriggeredEventDispatcher =
    PreferenceActivityTriggeredEventDispatcher()
  private val mMultiTapDetectedEventDispatcher = MultiTapDetectedEventDispatcher()
  private val mCustomEventReceivedEventDispatcher = CustomEventReceivedEventDispatcher()
  private val mEventDispatchers = arrayOf(
    mVisibilityChangedEventDispatcher,
    mOffsetsChangedEventDispatcher,
    mIsPreviewChangedEventDispatcher,
    mDesiredSizeChangedEventDispatcher,
    mPreferenceChangedEventDispatcher,
    mPreferenceActivityTriggeredEventDispatcher,
    mMultiTapDetectedEventDispatcher,
    mCustomEventReceivedEventDispatcher,
  )

  /**
   * Registers an event listener.
   * Note: Called from C# code.
   *
   * @param listener
   */
  fun registerLiveWallpaperEventsListener(listener: ILiveWallpaperEventsListener?) {
    requireNotNull(listener) { "listener == null" }
    if (mLiveWallpaperEventsListeners.contains(listener)) return
    mLiveWallpaperEventsListeners.add(listener)
  }

  /**
   * Unregisters an event listener.
   * Note: Called from C# code.
   *
   * @param listener
   */
  fun unregisterLiveWallpaperEventsListener(listener: ILiveWallpaperEventsListener?) {
    requireNotNull(listener) { "listener == null" }
    mLiveWallpaperEventsListeners.remove(listener)
  }

  /**
   * Notifies listeners about whether the wallpaper has become visible or not visible.
   *
   * @param isVisible
   */
  override fun visibilityChanged(isVisible: Boolean) {
    mVisibilityChangedEventDispatcher.Enqueue(VisibilityChangedEvent(isVisible))
  }

  /**
   * Notifies listeners about whether the wallpaper has entered or exited the preview mode.
   *
   * @param isPreview
   */
  override fun isPreviewChanged(isPreview: Boolean) {
    mIsPreviewChangedEventDispatcher.Record(isPreview)
  }

  /**
   * Notifies listeners about wallpaper desired size change.
   *
   * @param desiredWidth
   * @param desiredHeight
   */
  override fun desiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
    mDesiredSizeChangedEventDispatcher.Record(desiredWidth, desiredHeight)
  }

  /**
   * Notifies listeners about wallpaper offsets change.
   *
   * @param xOffset
   * @param yOffset
   * @param xOffsetStep
   * @param yOffsetStep
   * @param xPixelOffset
   * @param yPixelOffset
   */
  override fun offsetsChanged(
    xOffset: Float,
    yOffset: Float,
    xOffsetStep: Float,
    yOffsetStep: Float,
    xPixelOffset: Int,
    yPixelOffset: Int,
  ) {
    mOffsetsChangedEventDispatcher.Record(
      xOffset,
      yOffset,
      xOffsetStep,
      yOffsetStep,
      xPixelOffset,
      yPixelOffset,
    )
  }

  /**
   * Called to inform that live wallpaper preferences Activity has started.
   */
  override fun preferencesActivityTriggered() {
    mPreferenceActivityTriggeredEventDispatcher.Enqueue(PreferenceActivityTriggeredEvent())
  }

  /**
   * Notifies listeners about preference value change.
   *
   * @param key SharedPreferences preference key that has changed.
   */
  override fun preferenceChanged(key: String) {
    mPreferenceChangedEventDispatcher.Enqueue(PreferenceChangedEvent(key))
  }

  /**
   * Called to inform about a custom event.
   *
   * @param eventName Name of the event.
   * @param eventData Event data.
   */
  override fun customEventReceived(eventName: String, eventData: String) {
    mCustomEventReceivedEventDispatcher.Enqueue(CustomEventReceivedEvent(eventName, eventData))
  }

  /**
   * Called to inform that user has tapped the screen multiple times.
   */
  override fun multiTapDetected(finalTapPositionX: Float, finalTapPositionY: Float) {
    mMultiTapDetectedEventDispatcher.Record(finalTapPositionX, finalTapPositionY)
  }

  /**
   * Sends stored events to the listeners.
   * Note: Called from C# code.
   */
  fun dispatchEvents() {
    for (eventDispatcher in mEventDispatchers) {
      eventDispatcher.Dispatch()
    }
  }

  /* Base event dispatcher classes */
  private abstract inner class EventBase<T> {
    abstract fun isEqual(other: T): Boolean
  }

  private abstract inner class EventDispatcher {
    abstract fun Dispatch()
  }

  private abstract inner class QueuedEventDispatcher<T : EventBase<T>> : EventDispatcher() {
    private val _queue = LinkedBlockingDeque<T>()
    override fun Dispatch() {
      synchronized(_queue) {
        while (_queue.size > 0) {
          val event = _queue.remove()
          for (listener in mLiveWallpaperEventsListeners) {
            DispatchEvent(listener, event)
          }
        }
      }
    }

    fun Enqueue(event: T): Boolean {
      synchronized(_queue) {
        if (_queue.size == 0 || !_queue.peekLast()!!.isEqual(event)) {
          _queue.offer(event)
          return true
        }
        return false
      }
    }

    protected abstract fun DispatchEvent(
      listener: ILiveWallpaperEventsListener,
      event: T,
    )
  }

  private abstract inner class ImmediateEventDispatcher : EventDispatcher() {
    protected var mIsDispatched = false
    protected var mHasValue = false
    override fun Dispatch() {
      if (!mHasValue || mIsDispatched) return
      synchronized(this) {
        for (listener in mLiveWallpaperEventsListeners) {
          DispatchEvent(listener)
        }
        mIsDispatched = true
        mHasValue = false
      }
    }

    protected abstract fun DispatchEvent(listener: ILiveWallpaperEventsListener)
  }

  /* VisibilityChanged */
  private inner class VisibilityChangedEvent(val IsVisible: Boolean) :
    EventBase<VisibilityChangedEvent>() {
    override fun isEqual(other: VisibilityChangedEvent): Boolean {
      return IsVisible == other.IsVisible
    }
  }

  private inner class VisibilityChangedEventDispatcher :
    QueuedEventDispatcher<VisibilityChangedEvent>() {
    override fun DispatchEvent(
      listener: ILiveWallpaperEventsListener,
      event: VisibilityChangedEvent,
    ) {
      listener.visibilityChanged(event.IsVisible)
    }
  }

  /* OffsetsChanged */
  private inner class OffsetsChangedEventDispatcher : ImmediateEventDispatcher() {
    private var mXOffset = 0f
    private var mYOffset = 0f
    private var mXOffsetStep = 0f
    private var mYOffsetStep = 0f
    private var mXPixelOffset = 0
    private var mYPixelOffset = 0
    fun Record(
      xOffset: Float,
      yOffset: Float,
      xOffsetStep: Float,
      yOffsetStep: Float,
      xPixelOffset: Int,
      yPixelOffset: Int,
    ): Boolean {
      synchronized(this) {
        var result = !mHasValue
        if (mXOffset != xOffset || mYOffset != yOffset || mXOffsetStep != xOffsetStep || mYOffsetStep != yOffsetStep || mXPixelOffset != xPixelOffset || mYPixelOffset != yPixelOffset) {
          result = true
        }
        if (result) {
          mHasValue = true
          mIsDispatched = false
          mXOffset = xOffset
          mYOffset = yOffset
          mXOffsetStep = xOffsetStep
          mYOffsetStep = yOffsetStep
          mXPixelOffset = xPixelOffset
          mYPixelOffset = yPixelOffset
        }
        return result
      }
    }

    override fun DispatchEvent(listener: ILiveWallpaperEventsListener) {
      listener.offsetsChanged(
        mXOffset,
        mYOffset,
        mXOffsetStep,
        mYOffsetStep,
        mXPixelOffset,
        mYPixelOffset,
      )
    }
  }

  /* IsPreviewChanged */
  private inner class IsPreviewChangedEventDispatcher : ImmediateEventDispatcher() {
    private var mIsPreview = false
    fun Record(isPreview: Boolean): Boolean {
      synchronized(this) {
        var result = !mHasValue
        if (mIsPreview != isPreview) {
          result = true
        }
        if (result) {
          mHasValue = true
          mIsDispatched = false
          mIsPreview = isPreview
        }
        return result
      }
    }

    override fun DispatchEvent(listener: ILiveWallpaperEventsListener) {
      listener.isPreviewChanged(mIsPreview)
    }
  }

  /* DesiredSizeChanged */
  private inner class DesiredSizeChangedEventDispatcher : ImmediateEventDispatcher() {
    private var mDesiredWidth = 0
    private var mDesiredHeight = 0
    fun Record(desiredWidth: Int, desiredHeight: Int): Boolean {
      synchronized(this) {
        var result = !mHasValue
        if (mDesiredWidth != desiredWidth ||
          mDesiredHeight != desiredHeight
        ) {
          result = true
        }
        if (result) {
          mHasValue = true
          mIsDispatched = false
          mDesiredWidth = desiredWidth
          mDesiredHeight = desiredHeight
        }
        return result
      }
    }

    override fun DispatchEvent(listener: ILiveWallpaperEventsListener) {
      listener.desiredSizeChanged(mDesiredWidth, mDesiredHeight)
    }
  }

  /* PreferenceChanged */
  private inner class PreferenceChangedEvent(val PreferenceKey: String) :
    EventBase<PreferenceChangedEvent>() {
    override fun isEqual(other: PreferenceChangedEvent): Boolean {
      return PreferenceKey == other.PreferenceKey
    }
  }

  private inner class PreferenceChangedEventDispatcher :
    QueuedEventDispatcher<PreferenceChangedEvent>() {
    override fun DispatchEvent(
      listener: ILiveWallpaperEventsListener,
      event: PreferenceChangedEvent,
    ) {
      listener.preferenceChanged(event.PreferenceKey)
    }
  }

  /* PreferenceActivityTriggered */
  private inner class PreferenceActivityTriggeredEvent :
    EventBase<PreferenceActivityTriggeredEvent>() {
    override fun isEqual(other: PreferenceActivityTriggeredEvent): Boolean {
      // Each event must be added into the queue
      return false
    }
  }

  private inner class PreferenceActivityTriggeredEventDispatcher :
    QueuedEventDispatcher<PreferenceActivityTriggeredEvent>() {
    override fun DispatchEvent(
      listener: ILiveWallpaperEventsListener,
      event: PreferenceActivityTriggeredEvent,
    ) {
      listener.preferencesActivityTriggered()
    }
  }

  /* MultiTapDetected */
  private inner class MultiTapDetectedEventDispatcher : ImmediateEventDispatcher() {
    private var mFinalTapPositionX = 0f
    private var mFinalTapPositionY = 0f
    fun Record(finalTapPositionX: Float, finalTapPositionY: Float): Boolean {
      synchronized(this) {
        var result = !mHasValue
        if (mFinalTapPositionX != finalTapPositionX ||
          mFinalTapPositionY != finalTapPositionY
        ) {
          result = true
        }
        if (result) {
          mHasValue = true
          mIsDispatched = false
          mFinalTapPositionX = finalTapPositionX
          mFinalTapPositionY = finalTapPositionY
        }
        return result
      }
    }

    override fun DispatchEvent(listener: ILiveWallpaperEventsListener) {
      listener.multiTapDetected(mFinalTapPositionX, mFinalTapPositionY)
    }
  }

  /* CustomEventReceived */
  private inner class CustomEventReceivedEvent(
    val name: String,
    val data: String,
  ) :
    EventBase<CustomEventReceivedEvent>() {
    override fun isEqual(other: CustomEventReceivedEvent): Boolean {
      // Each event must is added into queue
      return false
    }
  }

  private inner class CustomEventReceivedEventDispatcher :
    QueuedEventDispatcher<CustomEventReceivedEvent>() {
    override fun DispatchEvent(
      listener: ILiveWallpaperEventsListener,
      event: CustomEventReceivedEvent,
    ) {
      listener.customEventReceived(event.name, event.data)
    }
  }
}
