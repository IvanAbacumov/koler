package com.chooloo.www.callmanager.service

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.chooloo.www.callmanager.ui.activity.OngoingCallActivity
import com.chooloo.www.callmanager.util.CallManager

class CallService : InCallService() {
    /**
     * When call has been added
     *
     * @param call
     */
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        val intent = Intent(this, OngoingCallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        CallManager.sCall = call
    }

    /**
     * When call has been removed
     *
     * @param call
     */
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        CallManager.sCall = null
    }
}