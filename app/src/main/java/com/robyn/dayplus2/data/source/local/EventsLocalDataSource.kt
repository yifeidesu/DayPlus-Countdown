package com.robyn.dayplus2.data.source.local

import android.annotation.SuppressLint
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.EventsDataSource
import kotlinx.coroutines.experimental.launch

/**
 * Singleton
 * Created by yifei on 5/11/2017.
 */

class EventsLocalDataSource private constructor(val dao: EventsDao) : EventsDataSource {
    //private val mContext: Context = context.applicationContext
    //private val mDb: EventDatabase = EventDatabase.getInMemoryDatabase()

    override suspend fun insertEvent(event: MyEvent) {
        dao.insertEvent(event)
        //mDb.eventsDao().insertEvent(getTheEvent(eventId))
    }



    override suspend fun fetchEvent(eventId: String): MyEvent = dao.getEventById(eventId)

    override suspend fun deleteEvent(event: MyEvent) {
        dao.deleteEvent(event)
    }

     fun deleteAllEvents() {

        val events = dao.all()
        dao.deleteAllEvents(events)
    }

    override suspend fun removeEvent(eventId: String) {
        dao.deleteEventById(eventId)
    }

    override suspend fun fetchAllEvents(): ArrayList<MyEvent> = dao.all() as ArrayList<MyEvent>

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback) {

        val events = dao.all()

        if (events.isEmpty()) {
            callback.onDataNotAvailable()
        } else {
            callback.onEventsLoaded(events)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: EventsLocalDataSource? = null

        // getInstance
        fun getInstance(eventsDao: EventsDao): EventsLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = EventsLocalDataSource(eventsDao)
            }

            return INSTANCE!!
        }

        fun clearInstance() {
            INSTANCE == null
        }
    }
}

