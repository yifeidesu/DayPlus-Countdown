package com.robyn.dayplus2.display

import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.EventsDataSource
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by yifei on 11/23/2017.
 */
class DisplayPresenter(
    private val eventId: String, // if get mUuid from fg arg, there is a arg nullable thing
    private val dataSource: EventsDataSource,
    private val view: DisplayContract.View
) : DisplayContract.Presenter {

    private lateinit var mEvent: MyEvent

    init {
        view.mPresenter = this
        loadEvent()
    }

    override fun loadEvent() {
        mEvent = runBlocking { async { dataSource.fetchEvent(eventId) }.await() }
    }

    override fun fetchEvent(uuidString: String): MyEvent {
        return runBlocking { async { dataSource.fetchEvent(uuidString) }.await() }
    }

    override fun getEvent(): MyEvent {
        return mEvent
    }

    override fun deleteEvent() {
        launch { dataSource.deleteEvent(mEvent) }
        // view.ac.finish
        // todo events ac on result update list.
    }

    override fun positionToUuid(position: Int): String {
        return runBlocking { async { dataSource.fetchAllEvents()[position].uuid }.await() }
    }

    var events = runBlocking { async { dataSource.fetchAllEvents() }.await() }

    override fun uuidToPosition(uuid: String): Int {
        val event = runBlocking { async { dataSource.fetchEvent(uuid) }.await() }
        return events.indexOf(event)
    }

    override fun getEventsCount(): Int {
        return runBlocking { async { dataSource.fetchAllEvents().size }.await() }
    }

    override fun setFragmentContent() {
        view.setDisplayContent(mEvent)
    }

    override fun updateToolbar() {
        view.updateToolbar(mEvent)
    }
}