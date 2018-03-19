package com.robyn.dayplus2.data.source

import com.robyn.dayplus2.data.MyEvent

interface EventsDataSource {

    interface LoadEventsCallback {

        fun onEventsLoaded(Events: List<MyEvent>)

        fun onDataNotAvailable()
    }

    interface GetEventCallback {

       // fun onDataNotAvailable()
    }

    fun getEvents(callback: LoadEventsCallback)

    suspend fun insertEvent(event: MyEvent)

    suspend fun fetchEvent(eventId: String): MyEvent

    suspend fun fetchAllEvents(): ArrayList<MyEvent>

    suspend fun removeEvent(eventId: String)

    suspend fun deleteEvent(event: MyEvent)
}