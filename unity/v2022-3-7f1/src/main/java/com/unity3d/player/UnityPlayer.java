package com.unity3d.player;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.unity3d.player.components.UnityFrameLayout;
import com.unity3d.player.components.UnitySplashView;
import com.unity3d.player.utils.LowMemoryRunnable;
import com.unity3d.player.utils.PhoneStateChangeListener;
import com.unity3d.player.utils.models.FocusState;
import com.unity3d.player.utils.models.UnityMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import dev.teogor.drifter.unity.common.BaseUnityPlayer;

/**
 * @noinspection ALL
 */
public class UnityPlayer extends BaseUnityPlayer implements IUnityPlayerLifecycleEvents {
  public static final int ANR_TIMEOUT_SECONDS = 4;
  private static final int RUN_STATE_CHANGED_MSG_CODE = 2269;
  private static final String SPLASH_ENABLE_METADATA_NAME = "unity.splash-enable";
  private static final String SPLASH_MODE_METADATA_NAME = "unity.splash-mode";
  private static final String LAUNCH_FULLSCREEN = "unity.launch-fullscreen";
  private static final String ARCORE_ENABLE_METADATA_NAME = "unity.arcore-enable";
  private static final String AUTO_REPORT_FULLY_DRAWN_ENABLE_METADATA_NAME = "unity.auto-report-fully-drawn";
  public static Activity currentActivity;

  static {
    new G().a();
  }

  private final ConcurrentLinkedQueue<Runnable> m_Events;
  Handler mHandler;
  UnityThread m_MainThread;
  B mSoftInputDialog;
  private int mInitialScreenOrientation;
  private boolean mMainDisplayOverride;
  private boolean mIsFullscreen;
  private J mState;
  private BroadcastReceiver mKillingIsMyBusiness;
  private OrientationEventListener mOrientationListener;
  private int mNaturalOrientation;
  private boolean m_AddPhoneCallListener;
  private PhoneStateChangeListener m_PhoneCallListener;
  private TelephonyManager m_TelephonyManager;
  private ClipboardManager m_ClipboardManager;
  private UnitySplashView m_SplashScreen;
  private GoogleARCoreApi m_ARCoreApi;
  private SensorEventListenerUnity m_FakeListener;
  private Camera2Wrapper m_Camera2Wrapper;
  private HFPStatus m_HFPStatus;
  private AudioVolumeHandler m_AudioVolumeHandler;
  private OrientationLockListener m_OrientationLockListener;
  private Uri m_launchUri;
  private NetworkConnectivity m_NetworkConnectivity;
  private IUnityPlayerLifecycleEvents m_UnityPlayerLifecycleEvents;
  private int m_IsNoWindowMode;
  private Context mContext;
  private Activity mActivity;
  private UnityFrameLayout mGlView;
  private boolean mQuitting;
  private boolean mProcessKillRequested;
  private U mVideoPlayerProxy;

  public UnityPlayer(Context context) {
    this(context, null);
  }

  public UnityPlayer(Context context, IUnityPlayerLifecycleEvents iUnityPlayerLifecycleEvents) {
    super(context);
    this.mHandler = new Handler();
    this.mInitialScreenOrientation = -1;
    this.mMainDisplayOverride = false;
    this.mIsFullscreen = true;
    this.mState = new J();
    this.m_Events = new ConcurrentLinkedQueue<>();
    this.mKillingIsMyBusiness = null;
    this.mOrientationListener = null;
    this.m_MainThread = new UnityThread();
    this.m_AddPhoneCallListener = false;
    this.m_PhoneCallListener = new PhoneStateChangeListener(context, this);
    this.m_ARCoreApi = null;
    this.m_FakeListener = new SensorEventListenerUnity(this);
    this.m_Camera2Wrapper = null;
    this.m_HFPStatus = null;
    this.m_AudioVolumeHandler = null;
    this.m_OrientationLockListener = null;
    this.m_launchUri = null;
    this.m_NetworkConnectivity = null;
    this.m_UnityPlayerLifecycleEvents = null;
    this.m_IsNoWindowMode = -1;
    this.mProcessKillRequested = true;
    this.mSoftInputDialog = null;
    this.m_UnityPlayerLifecycleEvents = iUnityPlayerLifecycleEvents == null ? this : iUnityPlayerLifecycleEvents;
    G.a(getUnityNativeLibraryPath(context));
    if (context instanceof Activity) {
      Activity activity = (Activity) context;
      this.mActivity = activity;
      currentActivity = activity;
      this.mInitialScreenOrientation = activity.getRequestedOrientation();
      this.m_launchUri = this.mActivity.getIntent().getData();
    } else if (currentActivity != null) {

    }
    this.mContext = context;
    // todo UnityKtB --full-screen
    //  EarlyEnableFullScreenIfEnabled();
    this.mNaturalOrientation = getNaturalOrientation(getResources().getConfiguration().orientation);
    if (this.mActivity != null && getSplashEnabled()) {
      UnitySplashView c = new UnitySplashView(
        this.mContext,
        UnitySplashView.UnitySplashMode.values()[getSplashMode()]
      );
      this.m_SplashScreen = c;
      addView(c);
    }
    preloadJavaPlugins();
    String loadNative = loadNative(getUnityNativeLibraryPath(this.mContext));
    if (!J.d()) {
      // AbstractC0024u.Log(6, "Your hardware does not support this application.");
      AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle("Failure to initialize!").setPositiveButton("OK", new UnityDialogClickListener()).setMessage("Your hardware does not support this application.\n\n" + loadNative + "\n\n Press OK to quit.").create();
      create.setCancelable(false);
      create.show();
      return;
    }
    initJni(context);
    this.mState.d(true);
    UnityFrameLayout unityFrameLayout2 = new UnityFrameLayout(context, this);
    this.mGlView = unityFrameLayout2;
    addView(unityFrameLayout2);
    bringChildToFront(this.m_SplashScreen);
    this.mQuitting = false;
    // todo UnityKtB --full-screen
    //  hideStatusBar();
    this.m_TelephonyManager = (TelephonyManager) this.mContext.getSystemService(Context.TELEPHONY_SERVICE);
    this.m_ClipboardManager = (ClipboardManager) this.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    this.m_Camera2Wrapper = new Camera2Wrapper(this.mContext);
    this.m_HFPStatus = new HFPStatus(this.mContext);
    this.m_MainThread.start();
  }

  /**
   * Sends a message to a Unity GameObject with a specified method name and parameter.
   *
   * @param gameObject The name of the Unity GameObject to receive the message.
   * @param methodName The name of the method to invoke on the GameObject.
   * @param funcParam  The parameter to pass to the method as a UTF-8 encoded string.
   * @throws UnsupportedOperationException If native libraries are not loaded.
   */
  public static void UnitySendMessage(String gameObject, String methodName, String funcParam) {
    if (!J.d()) {
      // AbstractC0024u.Log(5, "Native libraries not loaded - dropping message for " + str + "." + str2);
      return;
    }
    nativeUnitySendMessage(gameObject, methodName, funcParam.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Sends a message to a Unity GameObject with a specified method name and parameter.
   *
   * <p>This method is native and should be implemented in a platform-specific manner.</p>
   *
   * @param gameObject  The name of the Unity GameObject to receive the message.
   * @param methodName  The name of the method to invoke on the GameObject.
   * @param messageData The message data to pass to the method as a byte array.
   */
  private static native void nativeUnitySendMessage(String gameObject, String methodName, byte[] messageData);

  private static String logLoadLibMainError(String str, String str2) {
    String str3 = "Failed to load 'libmain.so'\n\n" + str2;
//    AbstractC0024u.Log(6, str3);
    return str3;
  }

  private static void preloadJavaPlugins() {
    try {
      Class.forName("com.unity3d.JavaPluginPreloader");
    } catch (ClassNotFoundException unused) {
    } catch (LinkageError e2) {
//      AbstractC0024u.Log(6, "Java class preloading failed: " + e2.getMessage());
    }
  }

  private static String loadNative(String paramString) {
    String str = "";
    try {
      System.load(str = paramString + "/libmain.so");
      if (NativeLoader.load(paramString)) {
        J.e();
        return "";
      }
//      u.Log(6, "NativeLoader.load failure, Unity libraries were not loaded.");
      return "NativeLoader.load failure, Unity libraries were not loaded.";
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      try {
        System.loadLibrary("main");
        if (NativeLoader.load(paramString)) {
          J.e();
          return "";
        }
//        u.Log(6, "NativeLoader.load failure, Unity libraries were not loaded.");
        return "NativeLoader.load failure, Unity libraries were not loaded.";
      } catch (UnsatisfiedLinkError unsatisfiedLinkError1) {
        return logLoadLibMainError(str, null);
      }
    } catch (SecurityException securityException) {
      return logLoadLibMainError(str, null);
    }
  }

  private static void unloadNative() {
    if (J.d()) {
      if (NativeLoader.unload()) {
        J.f();
        return;
      }
      throw new UnsatisfiedLinkError("Unable to unload libraries from libmain.so");
    }
  }

  private static String getUnityNativeLibraryPath(Context context) {
    return context.getApplicationInfo().nativeLibraryDir;
  }

  private int getNaturalOrientation(int i2) {
    int rotation = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    if ((rotation == 0 || rotation == 2) && i2 == 2) {
      return 0;
    }
    return ((rotation == 1 || rotation == 3) && i2 == 1) ? 0 : 1;
  }

  private String GetGlViewContentDescription(Context context) {
    return context.getResources().getString(context.getResources().getIdentifier("game_view_content_description", "string", context.getPackageName()));
  }

  private void DisableStaticSplashScreen() {
    runOnUiThread(new r());
  }

  private void EarlyEnableFullScreenIfEnabled() {
    View decorView;
    Activity activity = this.mActivity;
    if (activity != null && activity.getWindow() != null) {
      if ((getLaunchFullscreen() || activity.getIntent().getBooleanExtra("android.intent.extra.VR_LAUNCH", false)) && (decorView = activity.getWindow().getDecorView()) != null) {
        decorView.setSystemUiVisibility(
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
      }
    }
  }

  private boolean IsWindowTranslucent() {
    Activity activity = this.mActivity;
    // todo safety-check
    if (true)
      throw new RuntimeException("IsWindowTranslucent");
    if (activity == null) {
      return false;
    }
    TypedArray obtainStyledAttributes = activity.getTheme().obtainStyledAttributes(new int[]{16842840});
    boolean z2 = obtainStyledAttributes.getBoolean(0, false);
    obtainStyledAttributes.recycle();
    return z2;
  }

  private boolean updateDisplayInternal(int i2, Surface surface) {
    if (!J.d() || !this.mState.a()) {
      return false;
    }
    Semaphore semaphore = new Semaphore(0);
    t tVar = new t(i2, surface, semaphore);
    if (i2 != 0) {
      tVar.run();
    } else if (surface == null) {
      UnityThread unityThread = this.m_MainThread;
      Handler handler = unityThread.handler;
      if (handler != null) {
        Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, UnityMessage.SURFACE_LOST).sendToTarget();
        Message.obtain(unityThread.handler, tVar).sendToTarget();
      }
    } else {
      UnityThread unityThread2 = this.m_MainThread;
      Handler handler2 = unityThread2.handler;
      if (handler2 != null) {
        Message.obtain(handler2, tVar).sendToTarget();
        UnityMessage unityMessage = UnityMessage.SURFACE_ACQUIRED;
        Handler handler3 = unityThread2.handler;
        if (handler3 != null) {
          Message.obtain(handler3, RUN_STATE_CHANGED_MSG_CODE, unityMessage).sendToTarget();
        }
      }
    }
    if (surface != null || i2 != 0) {
      return true;
    }
    try {
      if (semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
        return true;
      }
//      AbstractC0024u.Log(5, "Timeout while trying detaching primary window.");
      return true;
    } catch (InterruptedException unused) {
//      AbstractC0024u.Log(5, "UI thread got interrupted while trying to detach the primary window from the Unity Engine.");
      return true;
    }
  }

  private void finish() {
    Activity activity = this.mActivity;
    if (activity != null && !activity.isFinishing()) {
      this.mActivity.finish();
    }
  }

  private void pauseUnity() {
    reportSoftInputStr(null, 1, true);
    if (this.mState.c() && !this.mState.b()) {
      if (J.d()) {
        Semaphore semaphore = new Semaphore(0);
        Runnable vVar = isFinishing() ? new ShutdownRunnable(semaphore) : new PauseAndShutdownRunnable(semaphore);
        UnityThread unityThread = this.m_MainThread;
        Handler handler = unityThread.handler;
        if (handler != null) {
          Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, UnityMessage.PAUSE).sendToTarget();
          Message.obtain(unityThread.handler, vVar).sendToTarget();
        }
        try {
          if (!semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
//            AbstractC0024u.Log(5, "Timeout while trying to pause the Unity Engine.");
          }
        } catch (InterruptedException unused) {
//          AbstractC0024u.Log(5, "UI thread got interrupted while trying to pause the Unity Engine.");
        }
        if (semaphore.drainPermits() > 0) {
          destroy();
        }
      }
      this.mState.c(false);
      this.mState.e(true);
      if (this.m_AddPhoneCallListener) {
        m_PhoneCallListener.registerPhoneListenerNone();
      }
    }
  }

  private void shutdown() {
    this.mProcessKillRequested = nativeDone();
    this.mState.d(false);
  }

  private void checkResumePlayer() {
    boolean z2 = false;
    Activity activity = this.mActivity;
    if (activity != null) {
      z2 = MultiWindowSupport.getAllowResizableWindow(activity);
    }
    if (this.mState.a(z2)) {
      this.mState.c(true);
      queueGLThreadEvent(new UnityPlayerRunnableA());
      UnityThread unityThread = this.m_MainThread;
      UnityMessage unityMessage = UnityMessage.RESUME;
      Handler handler = unityThread.handler;
      if (handler != null) {
        Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, unityMessage).sendToTarget();
      }
    }
  }

  private final native void initJni(Context context);

  private final native boolean nativeRender();

  private final native void nativeSetInputArea(int i2, int i3, int i4, int i5);

  private final native void nativeSetKeyboardIsVisible(boolean z2);

  private final native void nativeSetInputString(String str);

  private final native void nativeSetInputSelection(int i2, int i3);

  private final native void nativeSoftInputCanceled();

  private final native void nativeSoftInputLostFocus();

  private final native void nativeReportKeyboardConfigChanged();

  private final native boolean nativePause();

  private final native void nativeResume();

  public final native void nativeLowMemory();

  private final native void nativeApplicationUnload();

  private final native void nativeFocusChanged(boolean z2);

  private final native void nativeRecreateGfxState(int i2, Surface surface);

  private final native void nativeSendSurfaceChangedEvent();

  private final native boolean nativeDone();

  private final native void nativeSoftInputClosed();

  private final native boolean nativeInjectEvent(InputEvent inputEvent);

  private final native boolean nativeIsAutorotationOn();

  public final native void nativeMuteMasterAudio(boolean z2);

  private final native void nativeRestartActivityIndicator();

  private final native void nativeSetLaunchURL(String str);

  private final native void nativeOrientationChanged(int i2, int i3);

  private final native boolean nativeGetNoWindowMode();

  private String getProcessName() {
    int myPid = Process.myPid();
    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
    if (runningAppProcesses == null) {
      return null;
    }
    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
      if (runningAppProcessInfo.pid == myPid) {
        return runningAppProcessInfo.processName;
      }
    }
    return null;
  }

  private ApplicationInfo getApplicationInfo() {
    try {
      PackageManager packageManager = this.mContext.getPackageManager();
      String packageName = this.mContext.getPackageName();
      return packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      // Handle the exception, e.g., log an error or throw a custom exception
      e.printStackTrace();
      return null; // Return null or throw an exception as appropriate for your use case
    }
  }

  private ActivityInfo getActivityInfo() {
    try {
      PackageManager packageManager = this.mActivity.getPackageManager();
      ComponentName componentName = this.mActivity.getComponentName();
      return packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      // Handle the exception, e.g., log an error or throw a custom exception
      e.printStackTrace();
      return null; // Return null or throw an exception as appropriate for your use case
    }
  }

  private boolean getSplashEnabled() {
    try {
      return getApplicationInfo().metaData.getBoolean(SPLASH_ENABLE_METADATA_NAME);
    } catch (Exception unused) {
      return false;
    }
  }

  private boolean getARCoreEnabled() {
    try {
      return getApplicationInfo().metaData.getBoolean(ARCORE_ENABLE_METADATA_NAME);
    } catch (Exception unused) {
      return false;
    }
  }

  private boolean getLaunchFullscreen() {
    try {
      return getApplicationInfo().metaData.getBoolean(LAUNCH_FULLSCREEN);
    } catch (Exception unused) {
      return false;
    }
  }

  private boolean getHaveAndroidWindowSupport() {
    if (this.m_IsNoWindowMode == -1) {
      this.m_IsNoWindowMode = nativeGetNoWindowMode() ? 1 : 0;
    }
    return this.m_IsNoWindowMode == 1;
  }

  private boolean getAutoReportFullyDrawnEnabled() {
    try {
      return getApplicationInfo().metaData.getBoolean(AUTO_REPORT_FULLY_DRAWN_ENABLE_METADATA_NAME);
    } catch (Exception unused) {
      return false;
    }
  }

  private void queueGLThreadEvent(UnityRunnable unityRunnable) {
    if (!isFinishing()) {
      queueGLThreadEvent((Runnable) unityRunnable);
    }
  }

  private void hideStatusBar() {
    Activity activity = this.mActivity;
    if (activity != null) {
      Window window = activity.getWindow();
      if (window != null) {
        window.setFlags(
          WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
      }
    }
  }

  private void swapViews(View view, View view2) {
    boolean z2 = false;
    if (!this.mState.b()) {
      pause();
      z2 = true;
    }
    if (view != null) {
      ViewParent parent = view.getParent();
      if (!(parent instanceof UnityPlayer) || ((UnityPlayer) parent) != this) {
        if (parent instanceof ViewGroup) {
          ((ViewGroup) parent).removeView(view);
        }
        addView(view);
        bringChildToFront(view);
        view.setVisibility(View.VISIBLE);
      }
    }
    if (view2 != null && view2.getParent() == this) {
      view2.setVisibility(View.GONE);
      removeView(view2);
    }
    if (z2) {
      resume();
    }
  }

  // com.unity3d.player.IUnityPlayerLifecycleEvents
  @Override
  public void onUnityPlayerUnloaded() {
  }

  // com.unity3d.player.IUnityPlayerLifecycleEvents
  @Override
  public void onUnityPlayerQuitted() {
  }

  protected void toggleGyroscopeSensor(boolean z2) {
    SensorManager sensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
    Sensor defaultSensor = sensorManager.getDefaultSensor(11);
    if (z2) {
      sensorManager.registerListener(this.m_FakeListener, defaultSensor, 1);
    } else {
      sensorManager.unregisterListener(this.m_FakeListener);
    }
  }

  public void sendSurfaceChangedEvent() {
    if (J.d() && this.mState.a()) {
      s sVar = new s();
      Handler handler = this.m_MainThread.handler;
      if (handler != null) {
        Message.obtain(handler, sVar).sendToTarget();
      }
    }
  }

  public void updateGLDisplay(int i2, Surface surface) {
    if (!this.mMainDisplayOverride) {
      updateDisplayInternal(i2, surface);
    }
  }

  @Override
  public void onNewCurrentActivity(@NonNull Activity activity) {
    currentActivity = activity;
  }

  @Override
  public boolean displayChanged(int i2, Surface surface) {
    if (i2 == 0) {
      this.mMainDisplayOverride = surface != null;
      runOnUiThread(new u());
    }
    return updateDisplayInternal(i2, surface);
  }

  void runOnAnonymousThread(Runnable runnable) {
    new Thread(runnable).start();
  }

  void runOnUiThread(Runnable runnable) {
    Activity activity = this.mActivity;
    if (activity != null) {
      activity.runOnUiThread(runnable);
    } else if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      this.mHandler.post(runnable);
    } else {
      runnable.run();
    }
  }

  void postOnUiThread(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  public void init(int i2, boolean z2) {
  }

  public View getView() {
    return this;
  }

  public Bundle getSettings() {
    return Bundle.EMPTY;
  }

  @Override
  public void quit() {
    destroy();
  }

  public void newIntent(Intent intent) {
    this.m_launchUri = intent.getData();
    UnityThread unityThread = this.m_MainThread;
    UnityMessage unityMessage = UnityMessage.URL_ACTIVATED;
    Handler handler = unityThread.handler;
    if (handler != null) {
      Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, unityMessage).sendToTarget();
    }
  }

  public void destroy() {
    Camera2Wrapper camera2Wrapper = this.m_Camera2Wrapper;
    if (camera2Wrapper != null) {
      camera2Wrapper.a();
      this.m_Camera2Wrapper = null;
    }
    HFPStatus hFPStatus = this.m_HFPStatus;
    if (hFPStatus != null) {
      hFPStatus.a();
      this.m_HFPStatus = null;
    }
    NetworkConnectivity networkConnectivity = this.m_NetworkConnectivity;
    if (networkConnectivity != null) {
      networkConnectivity.a();
      this.m_NetworkConnectivity = null;
    }
    this.mQuitting = true;
    if (!this.mState.b()) {
      pause();
    }
    UnityThread unityThread = this.m_MainThread;
    UnityMessage unityMessage = UnityMessage.QUIT;
    Handler handler = unityThread.handler;
    if (handler != null) {
      Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, unityMessage).sendToTarget();
    }
    try {
      this.m_MainThread.join(4000);
    } catch (InterruptedException unused) {
      this.m_MainThread.interrupt();
    }
    BroadcastReceiver broadcastReceiver = this.mKillingIsMyBusiness;
    if (broadcastReceiver != null) {
      this.mContext.unregisterReceiver(broadcastReceiver);
    }
    this.mKillingIsMyBusiness = null;
    if (J.d()) {
      removeAllViews();
    }
    if (this.mProcessKillRequested) {
      this.m_UnityPlayerLifecycleEvents.onUnityPlayerQuitted();
      kill();
    }
    unloadNative();
  }

  @Override
  public void kill() {
    Process.killProcess(Process.myPid());
  }

  @Override
  public void pause() {
    GoogleARCoreApi googleARCoreApi = this.m_ARCoreApi;
    if (googleARCoreApi != null) {
      googleARCoreApi.pauseARCore();
    }
    U u2 = this.mVideoPlayerProxy;
    if (u2 != null) {
      u2.c();
    }
    AudioVolumeHandler audioVolumeHandler = this.m_AudioVolumeHandler;
    if (audioVolumeHandler != null) {
      audioVolumeHandler.a();
      this.m_AudioVolumeHandler = null;
    }
//    OrientationLockListener orientationLockListener = this.m_OrientationLockListener;
//    if (orientationLockListener != null) {
//      orientationLockListener.a();
//      this.m_OrientationLockListener = null;
//    }
    pauseUnity();
  }

  @Override
  public void resume() {
    GoogleARCoreApi googleARCoreApi = this.m_ARCoreApi;
    if (googleARCoreApi != null) {
      googleARCoreApi.resumeARCore();
    }
    this.mState.e(false);
    U u2 = this.mVideoPlayerProxy;
    if (u2 != null) {
      u2.d();
    }
    checkResumePlayer();
    if (J.d()) {
      nativeRestartActivityIndicator();
    }
    if (this.m_AudioVolumeHandler == null) {
      this.m_AudioVolumeHandler = new AudioVolumeHandler(this.mContext);
    }
//    if (this.m_OrientationLockListener == null && J.d()) {
//      this.m_OrientationLockListener = new OrientationLockListener(this.mActivity);
//    }
  }

  public void lowMemory() {
    if (J.d()) {
      queueGLThreadEvent(new LowMemoryRunnable(this));
    }
  }

  public void unload() {
    nativeApplicationUnload();
  }

  protected boolean skipPermissionsDialog() {
    Activity activity = this.mActivity;
    if (activity != null) {
      return UnityPermissions.skipPermissionsDialog(activity);
    }
    return false;
  }

  protected void requestUserAuthorization(String str) {
    if (str != null && !str.isEmpty() && this.mActivity != null) {
      UnityPermissions.ModalWaitForPermissionResponse modalWaitForPermissionResponse = new UnityPermissions.ModalWaitForPermissionResponse();
      UnityPermissions.requestUserPermissions(this.mActivity, new String[]{str}, modalWaitForPermissionResponse);
      modalWaitForPermissionResponse.waitForResponse();
    }
  }

  protected int getNetworkConnectivity() {
    NetworkConnectivity networkConnectivity = this.m_NetworkConnectivity;
    if (networkConnectivity != null) {
      return networkConnectivity.b();
    }
    if (PlatformSupport.NOUGAT_SUPPORT) {
      this.m_NetworkConnectivity = new NetworkConnectivityNougat(this.mContext);
    } else {
      this.m_NetworkConnectivity = new NetworkConnectivity(this.mContext);
    }
    return this.m_NetworkConnectivity.b();
  }

  @Override
  public void configurationChanged(Configuration configuration) {
    U u2 = this.mVideoPlayerProxy;
    if (u2 != null) {
      u2.b();
    }
  }

  @Override
  public void windowFocusChanged(boolean z2) {
    this.mState.b(z2);
    if (this.mState.a()) {
      B b = this.mSoftInputDialog;
      if (b == null || b.d) {
        if (z2) {
          UnityThread unityThread = this.m_MainThread;
          UnityMessage unityMessage2 = UnityMessage.FOCUS_GAINED;
          Handler handler = unityThread.handler;
          if (handler != null) {
            Message.obtain(handler, RUN_STATE_CHANGED_MSG_CODE, unityMessage2).sendToTarget();
          }
        } else {
          UnityThread unityThread2 = this.m_MainThread;
          UnityMessage unityMessage3 = UnityMessage.FOCUS_LOST;
          Handler handler2 = unityThread2.handler;
          if (handler2 != null) {
            Message.obtain(handler2, RUN_STATE_CHANGED_MSG_CODE, unityMessage3).sendToTarget();
          }
        }
        checkResumePlayer();
      }
    }
  }

  protected boolean loadLibrary(String str) {
    try {
      System.loadLibrary(str);
      return true;
    } catch (Exception unused) {
      return false;
    } catch (UnsatisfiedLinkError unused2) {
      return false;
    }
  }

  protected void addPhoneCallListener() {
    this.m_AddPhoneCallListener = true;
    m_PhoneCallListener.registerPhoneListenerCallState();
  }

  protected void showSoftInput(String str, int i2, boolean z2, boolean z3, boolean z4, boolean z5, String str2, int i3, boolean z6, boolean z7) {
    postOnUiThread(new UnityPlayerRunnableB(this, str, i2, z2, z3, z4, z5, str2, i3, z6, z7));
  }

  protected void hideSoftInput() {
    postOnUiThread(new UnityPlayerRunnableC());
  }

  protected void setSoftInputStr(String str) {
    runOnUiThread(new UnityPlayerRunnableD(str));
  }

  protected void setCharacterLimit(int i2) {
    runOnUiThread(new UnityPlayerRunnableE(i2));
  }

  protected void setHideInputField(boolean z2) {
    runOnUiThread(new UnityPlayerRunnableF(z2));
  }

  protected void setSelection(int i2, int i3) {
    runOnUiThread(new UnityPlayerRunnableG(i2, i3));
  }

  protected String getKeyboardLayout() {
    B b = this.mSoftInputDialog;
    if (b == null) {
      return null;
    }
    return b.a();
  }

  /* access modifiers changed from: protected */
  public void reportSoftInputStr(String str, int i2, boolean z2) {
    if (i2 == 1) {
      hideSoftInput();
    }
    queueGLThreadEvent((UnityRunnable) new UnityRunnableH(z2, str, i2));
  }

  /* access modifiers changed from: protected */
  public void reportSoftInputSelection(int i2, int i3) {
    queueGLThreadEvent((UnityRunnable) new UnityRunnableI(i2, i3));
  }

  /* access modifiers changed from: protected */
  public void reportSoftInputArea(Rect rect) {
    queueGLThreadEvent((UnityRunnable) new UnityRunnableJ(rect));
  }

  /* access modifiers changed from: protected */
  public void reportSoftInputIsVisible(boolean z2) {
    queueGLThreadEvent((UnityRunnable) new UnityRunnableL(z2));
  }

  protected String getClipboardText() {
    String str = "";
    ClipData primaryClip = this.m_ClipboardManager.getPrimaryClip();
    if (primaryClip != null) {
      str = primaryClip.getItemAt(0).coerceToText(this.mContext).toString();
    }
    return str;
  }

  protected void setClipboardText(String str) {
    this.m_ClipboardManager.setPrimaryClip(ClipData.newPlainText("Text", str));
  }

  protected String getLaunchURL() {
    Uri uri = this.m_launchUri;
    if (uri != null) {
      return uri.toString();
    }
    return null;
  }

  protected boolean initializeGoogleAr() {
    if (this.m_ARCoreApi != null || this.mActivity == null || !getARCoreEnabled()) {
      return false;
    }
    GoogleARCoreApi googleARCoreApi = new GoogleARCoreApi();
    this.m_ARCoreApi = googleARCoreApi;
    googleARCoreApi.initializeARCore(this.mActivity);
    if (this.mState.b()) {
      return false;
    }
    this.m_ARCoreApi.resumeARCore();
    return false;
  }

  protected boolean showVideoPlayer(String str, int i2, int i3, int i4, boolean z2, int i5, int i6) {
    if (this.mVideoPlayerProxy == null) {
      this.mVideoPlayerProxy = new U(this);
    }
    boolean a = this.mVideoPlayerProxy.a(this.mContext, str, i2, i3, i4, z2, (long) i5, (long) i6, new UnityRunnableM());
    if (a) {
      runOnUiThread(new UnityRunnableN());
    }
    return a;
  }

  protected void pauseJavaAndCallUnloadCallback() {
    runOnUiThread(new UnityRunnableO());
  }

  protected boolean isUaaLUseCase() {
    String callingPackage;
    Activity activity = this.mActivity;
    return (activity == null || (callingPackage = activity.getCallingPackage()) == null || !callingPackage.equals(this.mContext.getPackageName())) ? false : true;
  }

  protected int getUaaLLaunchProcessType() {
    String processName = getProcessName();
    return (processName == null || processName.equals(this.mContext.getPackageName())) ? 0 : 1;
  }

  protected int getSplashMode() {
    try {
      return getApplicationInfo().metaData.getInt(SPLASH_MODE_METADATA_NAME);
    } catch (Exception unused) {
      return 0;
    }
  }

  protected void executeGLThreadJobs() {
    while (true) {
      Runnable runnable = (Runnable) this.m_Events.poll();
      if (runnable != null) {
        runnable.run();
      } else {
        return;
      }
    }
  }

  protected void disableLogger() {
//    AbstractC0024u.a = true;
  }

  /* access modifiers changed from: package-private */
  public void queueGLThreadEvent(Runnable runnable) {
    if (J.d()) {
      if (Thread.currentThread() == this.m_MainThread) {
        runnable.run();
      } else {
        this.m_Events.add(runnable);
      }
    }
  }

  protected boolean isFinishing() {
    if (this.mQuitting) {
      return true;
    }
    Activity activity = this.mActivity;
    if (activity != null) {
      this.mQuitting = activity.isFinishing();
    }
    return this.mQuitting;
  }

  @Override
  public boolean injectEvent(InputEvent inputEvent) {
    if (!J.d()) {
      return false;
    }
    return nativeInjectEvent(inputEvent);
  }

  @Override // android.view.KeyEvent.Callback, android.view.View
  public boolean onKeyUp(int i2, KeyEvent keyEvent) {
    return injectEvent(keyEvent);
  }

  @Override // android.view.KeyEvent.Callback, android.view.View
  public boolean onKeyDown(int i2, KeyEvent keyEvent) {
    return injectEvent(keyEvent);
  }

  @Override // android.view.KeyEvent.Callback, android.view.View
  public boolean onKeyMultiple(int i2, int i3, KeyEvent keyEvent) {
    return injectEvent(keyEvent);
  }

  @Override // android.view.KeyEvent.Callback, android.view.View
  public boolean onKeyLongPress(int i2, KeyEvent keyEvent) {
    return injectEvent(keyEvent);
  }

  @Override // android.view.View
  public boolean onTouchEvent(MotionEvent motionEvent) {
    if (!this.mGlView.isSurfaceViewValid()) {
      return injectEvent(motionEvent);
    }
    return false;
  }

  @Override // android.view.View
  public boolean onGenericMotionEvent(MotionEvent motionEvent) {
    if (!this.mGlView.isSurfaceViewValid()) {
      return injectEvent(motionEvent);
    }
    return false;
  }

  public boolean addViewToPlayer(View view, boolean z2) {
    swapViews(view, z2 ? this.mGlView : null);
    boolean z3 = view.getParent() == this;
    boolean z4 = z2 && this.mGlView.getParent() == null;
    boolean z5 = this.mGlView.getParent() == this;
    boolean z6 = z3 && (z4 || z5);
    if (!z6) {
      if (!z3) {
//        AbstractC0024u.Log(6, "addViewToPlayer: Failure adding view to hierarchy");
      }
      if (!z4 && !z5) {
//        AbstractC0024u.Log(6, "addViewToPlayer: Failure removing old view from hierarchy");
      }
    }
    return z6;
  }

  public void removeViewFromPlayer(View view) {
    swapViews(this.mGlView, view);
    boolean z2 = view.getParent() == null;
    boolean z3 = this.mGlView.getParent() == this;
    if (!(z2 && z3)) {
      if (!z2) {
//        AbstractC0024u.Log(6, "removeViewFromPlayer: Failure removing view from hierarchy");
      }
      if (!z3) {
//        AbstractC0024u.Log(6, "removeVireFromPlayer: Failure agging old view to hierarchy");
      }
    }
  }

  public void reportError(String str, String str2) {
    StringBuilder sb = new StringBuilder();
    sb.append(str);
    sb.append(": ");
    sb.append(str2);
//    AbstractC0024u.Log(6, sb.toString());
  }

  public String getNetworkProxySettings(String str) {
    String str2;
    String str3;
    String str4;
    if (str.startsWith("http:")) {
      str4 = "http.proxyHost";
      str3 = "http.proxyPort";
      str2 = "http.nonProxyHosts";
    } else if (!str.startsWith("https:")) {
      return null;
    } else {
      str4 = "https.proxyHost";
      str3 = "https.proxyPort";
      str2 = "http.nonProxyHosts";
    }
    String property = System.getProperties().getProperty(str4);
    if (property == null || "".equals(property)) {
      return null;
    }
    StringBuilder sb = new StringBuilder(property);
    String property2 = System.getProperties().getProperty(str3);
    if (property2 != null && !"".equals(property2)) {
      sb.append(":").append(property2);
    }
    String property3 = System.getProperties().getProperty(str2);
    if (property3 != null && !"".equals(property3)) {
      sb.append('\n').append(property3);
    }
    return sb.toString();
  }

  public boolean startOrientationListener(int i2) {
    if (this.mOrientationListener != null) {
//      AbstractC0024u.Log(5, "Orientation Listener already started.");
      return false;
    }
    UnityOrientationEventListener unityOrientationEventListenerVar = new UnityOrientationEventListener(this.mContext, i2);
    this.mOrientationListener = unityOrientationEventListenerVar;
    if (unityOrientationEventListenerVar.canDetectOrientation()) {
      this.mOrientationListener.enable();
      return true;
    }
//    AbstractC0024u.Log(5, "Orientation Listener cannot detect orientation.");
    return false;
  }

  public boolean stopOrientationListener() {
    OrientationEventListener orientationEventListener = this.mOrientationListener;
    if (orientationEventListener == null) {
//      AbstractC0024u.Log(5, "Orientation Listener was not started.");
      return false;
    }
    orientationEventListener.disable();
    this.mOrientationListener = null;
    return true;
  }

  public void setMainSurfaceViewAspectRatio(float f2) {
    if (this.mGlView != null) {
      runOnUiThread(new UnityRunnableQ(f2));
    }
  }

  static class SensorEventListenerUnity implements SensorEventListener {
    SensorEventListenerUnity(UnityPlayer unityPlayer) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }
  }

  public class UnityThread extends Thread {
    Handler handler;
    int angleX;
    int angleY;
    boolean isPaused = false;
    boolean isSurfaceLost = false;
    FocusState focusState = FocusState.LOST;
    int frameCount = 0;
    int splashDelay = 5;

    private UnityThread() {
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
      setName("UnityMain");
      Looper.prepare();
      this.handler = new Handler(Looper.myLooper(), new UnityHandlerCallback());
      Looper.loop();
    }

    class UnityHandlerCallback implements Handler.Callback {
      UnityHandlerCallback() {
      }

      private void handleFocusState() {
        UnityThread unityThread = UnityThread.this;
        if (unityThread.focusState == FocusState.DEFERRED && unityThread.isSurfaceLost) {
          UnityPlayer.this.nativeFocusChanged(true);
          UnityThread.this.focusState = FocusState.GAINED;
        }
      }

      @Override // android.os.Handler.Callback
      public boolean handleMessage(Message message) {
        if (message.what != UnityPlayer.RUN_STATE_CHANGED_MSG_CODE) {
          return false;
        }
        UnityMessage unityMessage = (UnityMessage) message.obj;
        UnityMessage unityMessage2 = UnityMessage.NEXT_FRAME;
        if (unityMessage == unityMessage2) {
          UnityThread unityThread = UnityThread.this;
          unityThread.frameCount--;
          UnityPlayer.this.executeGLThreadJobs();
          UnityThread unityThread2 = UnityThread.this;
          if (!unityThread2.isPaused) {
            return true;
          }
          if (UnityPlayer.this.getHaveAndroidWindowSupport() && !UnityThread.this.isSurfaceLost) {
            return true;
          }
          UnityThread unityThread3 = UnityThread.this;
          int i = unityThread3.splashDelay;
          if (i >= 0) {
            if (i == 0) {
              if (UnityPlayer.this.getSplashEnabled()) {
                UnityPlayer.this.DisableStaticSplashScreen();
              }
              UnityPlayer unityPlayer = UnityPlayer.this;
              if (unityPlayer.mActivity != null && unityPlayer.getAutoReportFullyDrawnEnabled()) {
                UnityPlayer.this.mActivity.reportFullyDrawn();
              }
            }
            UnityThread.this.splashDelay--;
          }
          if (!UnityPlayer.this.isFinishing() && !UnityPlayer.this.nativeRender()) {
            UnityPlayer.this.finish();
          }
        } else if (unityMessage == UnityMessage.QUIT) {
          Looper.myLooper().quit();
        } else if (unityMessage == UnityMessage.RESUME) {
          UnityThread.this.isPaused = true;
        } else if (unityMessage == UnityMessage.PAUSE) {
          UnityThread.this.isPaused = false;
        } else if (unityMessage == UnityMessage.SURFACE_LOST) {
          UnityThread.this.isSurfaceLost = false;
        } else if (unityMessage == UnityMessage.SURFACE_ACQUIRED) {
          UnityThread.this.isSurfaceLost = true;
          handleFocusState();
        } else if (unityMessage == UnityMessage.FOCUS_LOST) {
          UnityThread unityThread4 = UnityThread.this;
          if (unityThread4.focusState == FocusState.GAINED) {
            UnityPlayer.this.nativeFocusChanged(false);
          }
          UnityThread.this.focusState = FocusState.LOST;
        } else if (unityMessage == UnityMessage.FOCUS_GAINED) {
          UnityThread.this.focusState = FocusState.DEFERRED;
          handleFocusState();
        } else if (unityMessage == UnityMessage.URL_ACTIVATED) {
          UnityPlayer unityPlayer2 = UnityPlayer.this;
          unityPlayer2.nativeSetLaunchURL(unityPlayer2.getLaunchURL());
        } else if (unityMessage == UnityMessage.ORIENTATION_ANGLE_CHANGE) {
          UnityThread unityThread5 = UnityThread.this;
          UnityPlayer.this.nativeOrientationChanged(unityThread5.angleX, unityThread5.angleY);
        }
        UnityThread unityThread6 = UnityThread.this;
        if (!unityThread6.isPaused || unityThread6.frameCount > 0) {
          return true;
        }
        Message.obtain(unityThread6.handler, UnityPlayer.RUN_STATE_CHANGED_MSG_CODE, unityMessage2).sendToTarget();
        UnityThread.this.frameCount++;
        return true;
      }
    }
  }

  public abstract class UnityRunnable implements Runnable {
    private UnityRunnable() {
    }

    @Override // java.lang.Runnable
    public final void run() {
      if (!UnityPlayer.this.isFinishing()) {
        execute();
      }
    }

    public abstract void execute();
  }

  public class UnityPlayerRunnableA implements Runnable {

    UnityPlayerRunnableA() {
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer.this.nativeResume();
      UnityPlayer.this.runOnUiThread(new UnityPlayerRunnableAInner());
    }

    class UnityPlayerRunnableAInner implements Runnable {
      UnityPlayerRunnableAInner() {
      }

      @Override // java.lang.Runnable
      public void run() {
        UnityFrameLayout unityFrameLayout = UnityPlayer.this.mGlView;
        if (unityFrameLayout != null) {
          unityFrameLayout.removeNativeView();
        }
      }
    }
  }

  class UnityPlayerRunnableB implements Runnable {
    final UnityPlayer unityPlayer;
    final String param1;
    final int param2;
    final boolean param3;
    final boolean param4;
    final boolean param5;
    final boolean param6;
    final String param7;
    final int param8;
    final boolean param9;
    final boolean param10;

    UnityPlayerRunnableB(UnityPlayer unityPlayer, String str, int param8, boolean z, boolean z2, boolean z3, boolean z4, String str2, int i2, boolean z5, boolean z6) {
      this.unityPlayer = unityPlayer;
      this.param1 = str;
      this.param2 = param8;
      this.param3 = z;
      this.param4 = z2;
      this.param5 = z3;
      this.param6 = z4;
      this.param7 = str2;
      this.param8 = i2;
      this.param9 = z5;
      this.param10 = z6;
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer.this.mSoftInputDialog = new B(UnityPlayer.this.mContext, this.unityPlayer, this.param1, this.param2, this.param3, this.param4, this.param5, this.param6, this.param7, this.param8, this.param9, this.param10);
      UnityPlayer.this.mSoftInputDialog.setOnCancelListener(new UnityPlayerRunnableBInner());
      UnityPlayer.this.mSoftInputDialog.show();
      UnityPlayer.this.nativeReportKeyboardConfigChanged();
    }

    class UnityPlayerRunnableBInner implements DialogInterface.OnCancelListener {
      UnityPlayerRunnableBInner() {
      }

      @Override // android.content.DialogInterface.OnCancelListener
      public void onCancel(DialogInterface dialogInterface) {
        UnityPlayer.this.nativeSoftInputLostFocus();
        UnityPlayer.this.reportSoftInputStr(null, 1, false);
      }
    }
  }

  public class UnityPlayerRunnableC implements Runnable {
    UnityPlayerRunnableC() {
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer.this.reportSoftInputArea(new Rect());
      UnityPlayer.this.reportSoftInputIsVisible(false);
      B b = UnityPlayer.this.mSoftInputDialog;
      if (b != null) {
        b.dismiss();
        UnityPlayer unityPlayer = UnityPlayer.this;
        unityPlayer.mSoftInputDialog = null;
        unityPlayer.nativeReportKeyboardConfigChanged();
      }
    }
  }

  class UnityPlayerRunnableD implements Runnable {
    final String param;

    UnityPlayerRunnableD(String str) {
      this.param = str;
    }

    @Override // java.lang.Runnable
    public void run() {
      String str;
      B dialog = UnityPlayer.this.mSoftInputDialog;
      if (dialog != null && (str = this.param) != null) {
        dialog.a(str);
      }
    }
  }

  class UnityPlayerRunnableE implements Runnable {
    final int inputValue;

    UnityPlayerRunnableE(int value) {
      this.inputValue = value;
    }

    @Override // java.lang.Runnable
    public void run() {
      B dialog = UnityPlayer.this.mSoftInputDialog;
      if (dialog != null) {
        dialog.a(this.inputValue);
      }
    }
  }

  class UnityPlayerRunnableF implements Runnable {
    final boolean isValue;

    UnityPlayerRunnableF(boolean value) {
      this.isValue = value;
    }

    @Override // java.lang.Runnable
    public void run() {
      B dialog = UnityPlayer.this.mSoftInputDialog;
      if (dialog != null) {
        dialog.a(this.isValue);
      }
    }
  }

  class UnityPlayerRunnableG implements Runnable {
    final int value1;
    final int value2;

    UnityPlayerRunnableG(int val1, int val2) {
      this.value1 = val1;
      this.value2 = val2;
    }

    @Override // java.lang.Runnable
    public void run() {
      B dialog = UnityPlayer.this.mSoftInputDialog;
      if (dialog != null) {
        dialog.a(this.value1, this.value2);
      }
    }
  }

  public class UnityRunnableH extends UnityRunnable {
    final boolean isCanceled;
    final String inputString;
    final int closeType;

    UnityRunnableH(boolean canceled, String str, int type) {
      super();
      this.isCanceled = canceled;
      this.inputString = str;
      this.closeType = type;
    }

    @Override
    public void execute() {
      if (this.isCanceled) {
        UnityPlayer.this.nativeSoftInputCanceled();
      } else {
        String str = this.inputString;
        if (str != null) {
          UnityPlayer.this.nativeSetInputString(str);
        }
      }
      if (this.closeType == 1) {
        UnityPlayer.this.nativeSoftInputClosed();
      }
    }
  }

  public class UnityRunnableI extends UnityRunnable {
    final int selectionStart;
    final int selectionEnd;

    UnityRunnableI(int start, int end) {
      super();
      this.selectionStart = start;
      this.selectionEnd = end;
    }

    @Override
    public void execute() {
      UnityPlayer.this.nativeSetInputSelection(this.selectionStart, this.selectionEnd);
    }
  }

  public class UnityRunnableJ extends UnityRunnable {
    final Rect inputArea;

    UnityRunnableJ(Rect rect) {
      super();
      this.inputArea = rect;
    }

    @Override
    public void execute() {
      UnityPlayer unityPlayer = UnityPlayer.this;
      Rect rect = this.inputArea;
      unityPlayer.nativeSetInputArea(rect.left, rect.top, rect.right, rect.bottom);
    }
  }

  class UnityDialogClickListener implements DialogInterface.OnClickListener {
    UnityDialogClickListener() {
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
      UnityPlayer.this.finish();
    }
  }

  public class UnityRunnableL extends UnityRunnable {
    final boolean isKeyboardVisible;

    UnityRunnableL(boolean isVisible) {
      super();
      this.isKeyboardVisible = isVisible;
    }

    @Override
    public void execute() {
      UnityPlayer.this.nativeSetKeyboardIsVisible(this.isKeyboardVisible);
    }
  }

  public class UnityRunnableM implements U.a {
    UnityRunnableM() {
    }

    public void a() {
      UnityPlayer.this.mVideoPlayerProxy = null;
    }
  }

  class UnityRunnableN implements Runnable {
    UnityRunnableN() {
    }

    @Override
    public void run() {
      UnityPlayer unityPlayer;
      Activity activity;
      if (UnityPlayer.this.nativeIsAutorotationOn() && (activity = (unityPlayer = UnityPlayer.this).mActivity) != null) {
        activity.setRequestedOrientation(unityPlayer.mInitialScreenOrientation);
      }
    }
  }

  class UnityRunnableO implements Runnable {
    UnityRunnableO() {
    }

    @Override
    public void run() {
      UnityPlayer.this.pause();
      UnityPlayer.this.windowFocusChanged(false);
      UnityPlayer.this.m_UnityPlayerLifecycleEvents.onUnityPlayerUnloaded();
    }
  }

  class UnityOrientationEventListener extends OrientationEventListener {
    UnityOrientationEventListener(Context context, int i) {
      super(context, i);
    }

    @Override
    public void onOrientationChanged(int i) {
      UnityPlayer unityPlayer = UnityPlayer.this;
      UnityThread unityThread = unityPlayer.m_MainThread;
      unityThread.angleX = unityPlayer.mNaturalOrientation;
      unityThread.angleY = i;
      UnityMessage unityMessage = UnityMessage.ORIENTATION_ANGLE_CHANGE;
      Handler handler = unityThread.handler;
      if (handler != null) {
        Message.obtain(handler, UnityPlayer.RUN_STATE_CHANGED_MSG_CODE, unityMessage).sendToTarget();
      }
    }
  }

  class UnityRunnableQ implements Runnable {
    final float angle;

    UnityRunnableQ(float angle) {
      this.angle = angle;
    }

    @Override
    public void run() {
      UnityPlayer.this.mGlView.setAspectRatio(this.angle);
    }
  }

  public class r implements Runnable {
    r() {
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer unityPlayer = UnityPlayer.this;
      unityPlayer.removeView(unityPlayer.m_SplashScreen);
      UnityPlayer.this.m_SplashScreen = null;
    }
  }

  class s implements Runnable {
    s() {
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer.this.nativeSendSurfaceChangedEvent();
    }
  }

  public class t implements Runnable {
    final int a;
    final Surface b;
    final Semaphore c;

    t(int i, Surface surface, Semaphore semaphore) {
      this.a = i;
      this.b = surface;
      this.c = semaphore;
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer.this.nativeRecreateGfxState(this.a, this.b);
      this.c.release();
    }
  }

  class u implements Runnable {
    u() {
    }

    @Override // java.lang.Runnable
    public void run() {
      UnityPlayer unityPlayer = UnityPlayer.this;
      if (unityPlayer.mMainDisplayOverride) {
        unityPlayer.removeView(unityPlayer.mGlView);
      } else {
        unityPlayer.addView(unityPlayer.mGlView);
      }
    }
  }

  public class ShutdownRunnable implements Runnable {
    final Semaphore semaphore;

    ShutdownRunnable(Semaphore semaphore) {
      this.semaphore = semaphore;
    }

    @Override
    public void run() {
      UnityPlayer.this.shutdown();
      this.semaphore.release();
    }
  }

  public class PauseAndShutdownRunnable implements Runnable {
    final Semaphore semaphore;

    PauseAndShutdownRunnable(Semaphore semaphore) {
      this.semaphore = semaphore;
    }

    @Override
    public void run() {
      if (UnityPlayer.this.nativePause()) {
        UnityPlayer unityPlayer = UnityPlayer.this;
        unityPlayer.mQuitting = true;
        unityPlayer.shutdown();
        this.semaphore.release(2);
      } else {
        this.semaphore.release();
      }
    }
  }
}
