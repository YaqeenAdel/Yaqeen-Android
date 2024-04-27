package com.cancer.yaqeen.presentation.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.util.Log
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import java.util.concurrent.TimeUnit


class ReminderRequest private constructor() {

    class Builder(private val context: Context) {
        private var startDateTime: Long = 0L
        private var periodTimeId: Int? = null
        private var title: String = ""
        private var body: String = ""
        private var actionName: String = ""
        private var requestCode: Int = 0
        private var objectJsonKey: String = ""
        private var objectJsonValue: String = ""
        private var alarmType: Int = AlarmManager.RTC_WAKEUP

        fun setStartDateTime(dateTime: Long): Builder {
            this.startDateTime = dateTime
            return this
        }

        fun setPeriodTime(periodTimeId: Int?): Builder {
            this.periodTimeId = periodTimeId
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setBody(body: String): Builder {
            this.body = body
            return this
        }

        fun setActionName(actionName: String): Builder {
            this.actionName = actionName
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun setObjectKey(objectJsonKey: String): Builder {
            this.objectJsonKey = objectJsonKey
            return this
        }

        fun <T> setObject(obj: T): Builder {
            this.objectJsonValue = obj.toJson()
            return this
        }

        fun setAlarmType(alarmType: Int): Builder {
            this.alarmType = alarmType
            return this
        }

        private fun createPendingIntent(): PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = actionName
//                flags = Intent.FLAG_RECEIVER_FOREGROUND
                data = Uri.parse(objectJsonValue)
            }

            val flags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            return PendingIntent.getBroadcast(context, requestCode, intent, flags)
        }

        fun build(): String {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createPendingIntent()
//            val bytes = serializePendingIntent(pendingIntent)
//            val parcel = unmarshall(bytes!!)
//            val pi = PendingIntent.readPendingIntentOrNullFromParcel(parcel)
//            Log.d("TAG", "buildUUID: ${pi == pendingIntent}")
            alarmManager.setRepeating(
                alarmType,
                startDateTime,
                getIntervalInMillis(periodTimeId),
                pendingIntent
            )

//            pi?.cancel()
//            Log.d("TAG", "cancelReminder1: $pendingIntent")
            return requestCode.toString()
        }
        fun serializePendingIntent(pendingIntent: PendingIntent?): ByteArray? {
            var parcel: Parcel? = null
            return try {
                parcel = Parcel.obtain()
                PendingIntent.writePendingIntentOrNullToParcel(pendingIntent, parcel)
                val bytes = parcel.createByteArray()
                bytes
            } finally {
                parcel?.recycle()
            }
        }

        fun unmarshall(bytes: ByteArray): Parcel {
            val parcel = Parcel.obtain()
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0) // this is extremely important!
            return parcel
        }
        fun buildOneTime(): String {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createPendingIntent()

            alarmManager.set(
                alarmType,
                startDateTime,
                pendingIntent
            )

            return requestCode.toString()
        }

    private fun getIntervalInMillis(periodTimeId: Int?): Long =
        when (periodTimeId) {
            PeriodTimeEnum.EVERY_DAY.id -> {
                getIntervalInMillisFromXDays(1)
            }

            PeriodTimeEnum.EVERY_8_HOURS.id -> {
                getIntervalInMillisFromXHours(8)
            }

            PeriodTimeEnum.EVERY_12_HOURS.id -> {
                getIntervalInMillisFromXHours(12)
            }

            PeriodTimeEnum.DAY_AFTER_DAY.id -> {
                getIntervalInMillisFromXDays(2)
            }

            PeriodTimeEnum.EVERY_WEEK.id -> {
                getIntervalInMillisFromXDays(7)
            }

            PeriodTimeEnum.EVERY_MONTH.id -> {
                getIntervalInMillisFromXDays(30)
            }

            PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> {
                getIntervalInMillisFromXDays(7)
            }

            else -> {
                getIntervalInMillisFromXDays(1)
            }
        }

    private fun getIntervalInMillisFromXDays(repeatInterval: Long): Long =
        TimeUnit.DAYS.toMillis(repeatInterval)

    private fun getIntervalInMillisFromXHours(repeatInterval: Long): Long =
        TimeUnit.HOURS.toMillis(repeatInterval)
    }

}