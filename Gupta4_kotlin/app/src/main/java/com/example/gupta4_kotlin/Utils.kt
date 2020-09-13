package com.example.gupta4_kotlin

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun getDiffTimeText(targetTime: Long): String {
        val curDateTime = DateTime()
        val targetDateTime = DateTime().withMillis(targetTime)

        val diffDay = Days.daysBetween(curDateTime, targetDateTime).days
        val diffHours = Hours.hoursBetween(targetDateTime, curDateTime).hours
        val diffMinutes = Minutes.minutesBetween(targetDateTime, curDateTime).minutes

        if(diffDay == 0) {
            if(diffHours == 0 && diffMinutes == 0) {
                return "방금 전"
            }

            return if(diffHours > 0) {
                "" + diffHours + "시간 전"
            } else {
                "" + diffMinutes + "분 전"
            }
        } else {
            val format = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm")
            return format.format(Date(targetTime))
        }
    }
}