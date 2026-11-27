package com.example.reelscounter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
        }
        val title = TextView(this).apply {
            text = "Reels Counter"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        val info = TextView(this).apply {
            text = "Counts detected scrolling events in Instagram and Facebook. Counts are estimates, not guaranteed Reel totals."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 24)
        }
        val accessibility = Button(this).apply {
            text = "1. Enable counting service"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val overlay = Button(this).apply {
            text = "2. Allow floating counter"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")))
                }
            }
        }
        val reset = Button(this).apply {
            text = "3. Reset counts"
            setOnClickListener { ReelsAccessibilityService.resetCounts() }
        }
        layout.addView(title); layout.addView(info)
        layout.addView(accessibility); layout.addView(overlay); layout.addView(reset)
        setContentView(layout)
    }
}
