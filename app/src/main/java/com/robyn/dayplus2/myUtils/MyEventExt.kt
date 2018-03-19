package com.robyn.dayplus2.myUtils

import android.content.Context
import android.widget.ImageView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.enums.EventCategory
import com.robyn.dayplus2.data.source.enums.EventRepeatMode
import com.robyn.dayplus2.data.source.enums.EventType
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.util.*

fun isSinceEvent(event: MyEvent): Boolean = (event.datetime - DateTime.now().millis) <= 0

// 0,1,2,3,4,5 = cake, loving, face, social, work, no categoryCode - to match the tab position in tabLayout
fun categoryCodeToFilter(eventCategoryCode: Int): EventCategory = when (eventCategoryCode) {
    0 -> EventCategory.CAKE_EVENTS
    1 -> EventCategory.LOVED_EVENTS
    2 -> EventCategory.FACE_EVENTS
    3 -> EventCategory.EXPLORE_EVENTS
    4 -> EventCategory.WORK_EVENTS
    else -> EventCategory.WORK_EVENTS // cannot return - if the receiver method
}

fun eventTypeToCode(eventType: EventType): Int = when (eventType) {
    EventType.ALL_EVENTS -> 0
    EventType.SINCE_EVENTS -> 1
    EventType.UNTIL_EVENTS -> 2
    EventType.STARRED_EVENTS -> 3
    EventType.CATEGORY_EVENTS -> 4
}

/**
 *  1,2,3,4,5 = cake, loving, face, social, work, no categoryCode - t
 *
 *  0 for no category. However for now try null for no category.
 *
 *  Since the db doesn't store enum type, use int code instead. Is it a good practice?
 */

/**
 * According to tabs' position code in the tabLayout, get this mapping:
 * position / categoryCode code -> tab 'name'
 * 0 -> cake
 * 1 -> loved
 * 2 -> face
 * 3 -> explore
 * 4 -> work
 */
//@Ignore
val categoryCodeMap: HashMap<EventCategory, Int> = hashMapOf(

    EventCategory.CAKE_EVENTS to 1,
    EventCategory.LOVED_EVENTS to 2,
    EventCategory.FACE_EVENTS to 3,
    EventCategory.EXPLORE_EVENTS to 4,
    EventCategory.WORK_EVENTS to 5

)

// repeatCode to enum
//@Ignore
//val repeatCodeMap: HashMap<Int, EventRepeatMode> = hashMapOf(
//    1 to EventRepeatMode.EVERY_WEEK,
//    2 to EventRepeatMode.EVERY_WEEK,
//    3 to EventRepeatMode.EVERY_WEEK
//)

// enum to repeat type string
//@Ignore
val repeatStrMap: HashMap<EventRepeatMode, String> = hashMapOf(
    EventRepeatMode.EVERY_WEEK to "Never",
    EventRepeatMode.EVERY_MONTH to "Every Week",
    EventRepeatMode.EVERY_YEAR to "Every Month"
)

fun MyEvent.repeatModeStr(): String {

    return repeatCodeToString(this.repeatMode)
}

fun repeatCodeToString(repeatCode: Int): String {
    return when (repeatCode) {
        1 -> "Every Week"
        2 -> "Every Month"
        3 -> "Every Year"
        else -> "Never"
    }
}

fun MyEvent.dayCount(): Int {
    val nowDate = DateTime.now()
    val eventDate = DateTime(this.datetime)
    val count = Days.daysBetween(
        nowDate.withTimeAtStartOfDay(),
        eventDate.withTimeAtStartOfDay()
    ).days
    return count
}

fun MyEvent.dayCountAbs(): Int = Math.abs(this.dayCount())

fun MyEvent.ifSince(): Int {
    return if (this.dayCount() > 0) {
        R.string.until
    } else {
        R.string.since
    }
}

fun MyEvent.ifSinceStr(): String {
    return if (this.dayCount() > 0) {
        "Until "
    } else {
        "Since "
    }
}

// Num days since date
fun MyEvent.daysSinceDateStr(): String {
    return "${this.dayCountAbs()} days ${ifSinceStr()} ${this.formatDateStr()}"
}

fun MyEvent.numDaysStr(): String {

    // Handle word pl.
    return if (this.dayCountAbs() > 1) {
        "${this.dayCountAbs()} days"
    } else {
        "${this.dayCountAbs()} day"
    }
}

fun MyEvent.photoFilename(): String = "IMG_${this.uuid}.jpg"

// For AddEditFragment date field.
fun MyEvent.formatDateStr(): String {
    val fmt = DateTimeFormat.forPattern("dd MMM, yyyy")
    return DateTime(this.datetime).toString(fmt)
}

fun MyEvent.formatDateStr(millis:Long): String {
    val fmt = DateTimeFormat.forPattern("dd MMM, yyyy")
    return DateTime(millis).toString(fmt)
}

fun MyEvent.getImageFile(context: Context): File? {

    with(File(context.filesDir, this.photoFilename())) {
        return if (this.exists()) {
            this
        } else {
            // retrieve or take photo?
//                val fileDir = context.filesDir
//                return File(fileDir, photoFilename)
            null
        }
    }
}

fun MyEvent.getImagePath(context: Context):String? {
    return getImageFile(context)?.path
}



