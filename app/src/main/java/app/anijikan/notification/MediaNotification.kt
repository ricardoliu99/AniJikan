package app.anijikan.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.anijikan.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            showNotification(
                context,
                context.getString(R.string.reminder_channel),
                intent?.getIntExtra("notificationId", 0)!!,
                intent.getStringExtra("textTitle")!!,
                intent.getStringExtra("textContent")!!
            )
        }
    }

}

@SuppressLint("UnspecifiedImmutableFlag")
fun setAlarm(
    context: Context,
    requestCode: Int,
    alarmTime: Long,
    notificationId: Int,
    textTitle: String,
    textContent: String
) {
    val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("textTitle", textTitle)
        intent.putExtra("textContent", textContent)
        PendingIntent.getBroadcast(context, requestCode, intent, 0)
    }

    alarmMgr.set(
        AlarmManager.RTC_WAKEUP,
        alarmTime,
        alarmIntent
    )
}

fun createNotificationChannel(channelId: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelId, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showNotification(
    context: Context,
    channelId: String,
    notificationId: Int,
    textTitle: String,
    textContent: String
) {
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_baseline_notifications)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}