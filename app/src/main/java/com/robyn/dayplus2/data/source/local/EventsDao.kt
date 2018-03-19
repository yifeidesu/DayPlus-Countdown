package com.robyn.dayplus2.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.robyn.dayplus2.data.MyEvent

/**
 * Created by yifei on 5/19/2017.
 *
 * data access obj
 */
@Dao
interface EventsDao {

    @Query("select * from myevent")
    fun all(): List<MyEvent>

    @Query("select * from myevent where uuid = :id")
    fun getEventById(id: String): MyEvent

    @Insert(onConflict = REPLACE)
    fun insertEvent(event: MyEvent)  // insert and replace

    @Delete
    fun deleteEvent(event: MyEvent)

    @Delete
    fun deleteAllEvents(events: List<MyEvent>)

    @Query("delete from myevent where uuid = :eventId")
    fun deleteEventById(eventId: String)
}
