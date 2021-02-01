package com.chooloo.www.callmanager.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chooloo.www.callmanager.ui.activity.OngoingCallActivity
import com.chooloo.www.callmanager.util.CallManager

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == OngoingCallActivity.ACTION_ANSWER) {
            // If the user pressed "Answer" from the notification
            CallManager.answer()
        } else if (action == OngoingCallActivity.ACTION_HANGUP) {
            // If the user pressed "Hang up" from the notification
            CallManager.reject()
        }
    }
}