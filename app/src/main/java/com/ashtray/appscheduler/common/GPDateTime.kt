package com.ashtray.appscheduler.common

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class GPDateTime {

    companion object {
        private const val TAG = "GPDateTime"
    }

    val dateValue: String
    var monthValue: String
    var yearValue: String
    var dateString: String

    var hourValue: String
    var minuteValue: String
    var timeString: String

    var dateTimeString: String
    var dateTimeLong: Long = 0L

    constructor(dateTimeLongValue: Long) {
        dateTimeLong = dateTimeLongValue
        dateTimeString = convertMillisecondsToDate(dateTimeLongValue)

        hourValue = dateTimeString.substring(0, 1)
        minuteValue = dateTimeString.substring(3, 4)
        timeString = "$hourValue:$minuteValue"

        dateValue = dateTimeString.substring(6, 7)
        monthValue = dateTimeString.substring(9, 10)
        yearValue = dateTimeString.substring(12,15)
        dateString = "$dateValue-$monthValue-$yearValue"
    }

    constructor(dateStringValue: String, timeStringValue: String) {
        dateString = dateStringValue

        val dateContainer = dateString.split("-")
        dateValue = dateContainer[0]
        monthValue = dateContainer[1]
        yearValue = dateContainer[2]

        timeString = timeStringValue
        val timeContainer = timeString.split(":")
        hourValue = timeContainer[0]
        minuteValue = timeContainer[1]

        dateTimeString = "$timeString $dateString"
        dateTimeLong = convertDateToMilliseconds(dateTimeString)
    }

    private fun convertDateToMilliseconds(dateTimeStringValue: String): Long {
        val dateObject = try {
            val format = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.ENGLISH)
            format.parse(dateTimeStringValue)
        } catch (e: Exception) {
            GPLog.e(TAG, "convertTimeToMilliseconds: Problem occurs [${e.message}]")
            e.printStackTrace()
            Date()
        }
        return dateObject.time
    }

    private fun convertMillisecondsToDate(dateTimeLongValue: Long): String {
        val format = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.ENGLISH)
        return format.format(Date(dateTimeLongValue))
    }
}