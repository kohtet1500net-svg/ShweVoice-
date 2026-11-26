package com.shwevoice.launcher.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AppControlAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: AppControlAccessibilityService? = null
            private set

        @Volatile
        var activePackageName: String? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val pkg = event.packageName?.toString()
            if (!pkg.isNullOrBlank() && pkg != packageName) {
                activePackageName = pkg
            }
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted by system
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    fun launchApp(targetPackage: String): Boolean {
        val launchIntent = packageManager.getLaunchIntentForPackage(targetPackage)
            ?: return false
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
        startActivity(launchIntent)
        return true
    }

    fun closeCurrentOrTargetApp(targetPackage: String?): Boolean {
        // 1. Return to Home screen cleanly
        val wentHome = performGlobalAction(GLOBAL_ACTION_HOME)

        // 2. If user wants to dismiss from Recents, open Recents and search for dismiss action
        if (!targetPackage.isNullOrBlank() && activePackageName == targetPackage) {
            performGlobalAction(GLOBAL_ACTION_RECENTS)
        }
        return wentHome
    }

    fun closeAllRecentApps(): Boolean {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
        val root: AccessibilityNodeInfo = rootInActiveWindow ?: return true
        val clearAllNodes = root.findAccessibilityNodeInfosByText("Clear all") +
            root.findAccessibilityNodeInfosByText("အားလုံးရှင်း")
        for (node in clearAllNodes) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }
        }
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun goHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }
}
