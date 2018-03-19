package com.robyn.dayplus2.data.source

import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource

/**
 * Created by yifei on 11/18/2017.
 */
class EventsRepository(private val eventsLocalDateSource: EventsLocalDataSource) :
    EventsDataSource {

    override suspend fun fetchEvent(eventId: String): MyEvent =
        eventsLocalDateSource.fetchEvent(eventId)

    override suspend fun fetchAllEvents(): ArrayList<MyEvent> {
        return eventsLocalDateSource.fetchAllEvents()
    }

    override suspend fun removeEvent(eventId: String) {
        eventsLocalDateSource.removeEvent(eventId)
    }

    override suspend fun deleteEvent(event: MyEvent) {
        eventsLocalDateSource.deleteEvent(event)
    }

    override fun getEvents(callback: EventsDataSource.LoadEventsCallback) {
    }

    override suspend fun insertEvent(event: MyEvent) {
        eventsLocalDateSource.insertEvent(event)
    }

    companion object {
        private var INSTANCE: EventsRepository? = null

        fun getInstance(eventsLocalDateSource: EventsLocalDataSource): EventsRepository {
            return INSTANCE ?: EventsRepository(eventsLocalDateSource).apply { INSTANCE = this }
        }
    }
}