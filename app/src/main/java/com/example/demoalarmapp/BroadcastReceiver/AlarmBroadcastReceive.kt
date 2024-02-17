package com.example.demoalarmapp.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.demoalarmapp.Service.AlarmService

class AlarmBroadcastReceive: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        Toast.makeText(context, "Alarm", Toast.LENGTH_LONG).show()
        Log.d("broad", "onReceive")
        val mIntent = Intent(context, AlarmService::class.java)
        mIntent.action = intent!!.action
        context!!.startForegroundService(mIntent)
    }

}