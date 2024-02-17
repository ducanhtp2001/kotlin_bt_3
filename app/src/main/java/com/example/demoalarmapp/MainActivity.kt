package com.example.demoalarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.demoalarmapp.BroadcastReceiver.AlarmBroadcastReceive
import com.example.demoalarmapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var alarmManage: AlarmManager

    val REQUEST_CODE_ALARM = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleEvenUI()

    }

    private fun handleEvenUI() {
        binding.timePiker.setOnTimeChangedListener { _, hour, minute ->
//            Toast.makeText(this, "time: $hour:$minute", Toast.LENGTH_SHORT).show()
            Log.d("Alarm", "time: $hour:$minute")
        }

        binding.btnSetAlarm.setOnClickListener {
            var hour = binding.timePiker.hour
            var minute = binding.timePiker.minute
//            Toast.makeText(this, "time: $hour:$minute", Toast.LENGTH_SHORT).show()
            Log.d("Alarm", "time: $hour:$minute")

            alarmManage = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent = Intent(this, AlarmBroadcastReceive::class.java)
            intent.action = "Alarm"
            var pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val calendarCurren = Calendar.getInstance()
            val calendarPick = Calendar.getInstance()

            if(hour > 12) {
                hour -= 12
                calendarPick.set(Calendar.HOUR, hour)
                calendarPick.set(Calendar.MINUTE, minute)
                calendarPick.set(Calendar.AM_PM, Calendar.PM)
            } else {
                calendarPick.set(Calendar.HOUR, hour)
                calendarPick.set(Calendar.MINUTE, minute)
                calendarPick.set(Calendar.AM_PM, Calendar.AM)
            }

            val result = calendarPick.compareTo(calendarCurren)

            val dateFormat = SimpleDateFormat("hh:mm a dd/MM/yyyy")

            when {
                result > 0 -> {
                    var time = dateFormat.format(calendarPick.time)
                    Toast.makeText(this
                    , "Set time: $time"
                    , Toast.LENGTH_SHORT).show()

                    alarmManage.set(AlarmManager.RTC_WAKEUP, calendarPick.timeInMillis, pendingIntent)
                }
                else -> {
                    calendarPick.add(Calendar.DAY_OF_MONTH, 1)
                    var time = dateFormat.format(calendarPick.time)
                    Toast.makeText(this
                        , "Set time: $time"
                        , Toast.LENGTH_SHORT).show()
                    alarmManage.setExact(AlarmManager.RTC_WAKEUP, calendarPick.timeInMillis, pendingIntent)
                }
            }
        }

        binding.btnSetAlarmInLate10seconds.setOnClickListener {
            alarmManage = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent = Intent(this, AlarmBroadcastReceive::class.java)
            intent.action = "ALARM"
            var pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val calendarPick = Calendar.getInstance()
            val timeInMillis = System.currentTimeMillis() + 10000
            alarmManage.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }
}