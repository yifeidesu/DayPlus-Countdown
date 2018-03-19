package com.robyn.dayplus2.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import org.joda.time.DateTime
import java.util.*

/**
 * Created by yifei on 5/11/2017.
 *
 * model + database scheme
 */
@Entity
class MyEvent {

    // fields in database scheme
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    var uuid: String //mUuid to string

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "bgImagePath")
    var bgImagePath: String? = null

    @ColumnInfo(name = "datetime")
    var datetime: Long = 0

    @ColumnInfo(name = "repeatMode")
    var repeatMode: Int = 0 // 0, 1, 2, 3 = never, week, month, year

    // 0,1,2,3,4,5 = cake, loving, face, social, work, no categoryCode -
    // to match the tab position in tabLayout
    @ColumnInfo(name = "categoryCode")
    var categoryCode: Int = 5

    @ColumnInfo(name = "isStar")
    var isStarred: Boolean = false

    constructor() {
        uuid = UUID.randomUUID().toString()
        datetime = DateTime().withTimeAtStartOfDay().millis // millis is long
    }

    @Ignore
    constructor(datetime: Long, title: String) {
        uuid = UUID.randomUUID().toString()
        this.datetime = datetime
        this.title = title
    }
}

