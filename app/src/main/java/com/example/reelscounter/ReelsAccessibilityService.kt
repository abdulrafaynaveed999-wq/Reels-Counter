package com.example.reelscounter

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView

class ReelsAccessibilityService : AccessibilityService() {
    private var windowManager: WindowManager? = null
    private var counterView: TextView? = null
    private var lastEvent = 0L
    private var instagramCount = 0
    private var facebookCount = 0

    override fun onServiceConnected() {
        instance = this
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            packageNames = arrayOf("com.instagram.android", "com.facebook.katana")
        }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showCounter()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
        val now = System.currentTimeMillis()
        if (now - lastEvent < 650) return
        lastEvent = now
        when (event.packageName?.toString()) {
            "com.instagram.android" -> instagramCount++
            "com.facebook.katana" -> facebookCount++
            else -> return
        }
        updateCounter()
    }

    private fun showCounter() {
        if (!android.provider.Settings.canDrawOverlays(this) || counterView != null) return
        val view = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xCC202020.toInt())
            setPadding(18, 12, 18, 12)
            text = counterText()
        }
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                   else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            type, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 12; y = 120 }
        try { windowManager?.addView(view, params); counterView = view } catch (_: Exception) {}
    }

    private fun counterText() = "Instagram: $instagramCount   Facebook: $facebookCount"
    private fun updateCounter() {
        if (counterView == null) showCounter()
        counterView?.text = counterText()
    }

    override fun onInterrupt() {}
    override fun onDestroy() {
        instance = null
        try { counterView?.let { windowManager?.removeView(it) } } catch (_: Exception) {}
        counterView = null
        super.onDestroy()
    }

    companion object {
        @Volatile private var instance: ReelsAccessibilityService? = null
        fun resetCounts() { instance?.let { it.instagramCount = 0; it.facebookCount = 0; it.updateCounter() } }
    }
}
