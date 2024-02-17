package com.example.demoalarmapp.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.demoalarmapp.BroadcastReceiver.AlarmBroadcastReceive
import com.example.demoalarmapp.R
import java.text.SimpleDateFormat
import java.util.Calendar

class AlarmService: Service() {

    lateinit var ACTION_ALARM: MyAction
    lateinit var ACTION_STOP: MyAction
    lateinit var ACTION_SMOOZE: MyAction
    val REQUEST_CODE_ALARM = 1


    val FORE_ID = 1

    lateinit var smf: SimpleDateFormat

    val CHANNEL_ID = "myChannel"
    private val TAG = "ForegroundService"
    lateinit var channel: NotificationChannel
    lateinit var notificationManager: NotificationManager

    private  lateinit var player : MediaPlayer

    lateinit var alarmManage: AlarmManager

    override fun onCreate() {
        super.onCreate()

        smf = SimpleDateFormat("hh:mm")

        ACTION_ALARM = MyAction(0, "ALARM")
        ACTION_STOP = MyAction(1, "STOP")
        ACTION_SMOOZE = MyAction(2, "SNOOZE")

        player = MediaPlayer()

        val resourceId = R.raw.pirates_of_the_caribbean
        val fileDescriptor = resources.openRawResourceFd(resourceId)
        if (fileDescriptor != null) {
            player.setDataSource(fileDescriptor.fileDescriptor, fileDescriptor.startOffset, fileDescriptor.length)
            fileDescriptor.close()

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            channel.setSound(null, null)
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification(applicationContext)

        when {
            intent!!.action == ACTION_ALARM.handle -> {
                alarm()
            }
            intent!!.action == ACTION_STOP.handle -> {
                stopAlarm()
            }
            intent!!.action == ACTION_SMOOZE.handle -> {
                smoozeAlarm()
            }
        }

        return START_STICKY
    }

    fun createNotification(context: Context) {
        val remoteView = RemoteViews(context.packageName, R.layout.notification_layout)
        remoteView.setTextViewText(R.id.txtTimeAlarm, getTime())
        remoteView.setOnClickPendingIntent(R.id.txtAlarmStop, handleClick(ACTION_STOP))
        remoteView.setOnClickPendingIntent(R.id.txtAlarmSnooze5Minutes, handleClick(ACTION_SMOOZE))

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm_icon)
            .setCustomContentView(remoteView)
            .setSound(null)
            .build()

        startForeground(FORE_ID, notification)
    }

    private fun handleClick(action: MyAction): PendingIntent? {
        var intent = Intent(this, AlarmBroadcastReceive::class.java)
        intent.action = action.handle
        return PendingIntent.getBroadcast(this, action.action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun alarm() {
        createNotification(this)
        player.prepare()
        player.start()
    }

    fun stopAlarm() {
        player.stop()
        stopSelf()
    }

    fun smoozeAlarm() {
        player.stop()

        alarmManage = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlarmBroadcastReceive::class.java)
        intent.action = "ALARM"
        var pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendarPick = Calendar.getInstance()
        val timeInMillis = System.currentTimeMillis() + 10000
        alarmManage.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        stopSelf()
    }

    private fun getTime(): String {
        val calendar = Calendar.getInstance()
        return smf.format(calendar.time).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
        Log.d(TAG, "Service is stop")
    }

    class MyAction(val action: Int, val handle: String)
}