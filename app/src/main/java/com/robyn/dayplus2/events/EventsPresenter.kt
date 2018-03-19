package com.robyn.dayplus2.events

import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.EventsDataSource
import com.robyn.dayplus2.data.source.EventsRepository
import com.robyn.dayplus2.data.source.enums.EventFilter
import com.robyn.dayplus2.data.source.enums.EventType
import com.robyn.dayplus2.myUtils.isSinceEvent
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

/**
 * Listen to ui actions of and update ui for [EventsFragment]; Get data from [EventsRepository]
 *
 * Created by yifei on 11/18/2017.
 */
class EventsPresenter(val dataSource: EventsDataSource, var eventsView: EventsContract.View) :
    EventsContract.Presenter {

    var mEvents: ArrayList<MyEvent>
    var mSubEvents: ArrayList<MyEvent> = ArrayList()
    var mEventsAdapter: EventsAdapter

    var mAscSorting: Boolean = true

    // Init code that not included in the  primary constructor
    init {
        eventsView.mPresenter = this

        // init events arraylist and recyclerview adapter, assign events to adapter.
        mEvents = runBlocking { async { queryEvents() }.await() }

        mEventsAdapter = EventsAdapter(mEvents)
    }



    override var eventsAdapter: EventsAdapter = mEventsAdapter

    // todo delete from base presenter
    override fun bind() {}

    /**
     * When to query db for events:
     *
     *  1, init presenter inst
     *  2, update events list? do i need to query again
     *  3, events activity onresume, update list, do i need to query again?
     */
    //private fun queryEvents() = runBlocking { async { dataSource.fetchAllEvents() }.await() }

    suspend fun queryEvents(): ArrayList<MyEvent> {
        return dataSource.fetchAllEvents()
    }

    override var filter: EventFilter = EventType.ALL_EVENTS

    override fun updateEvents(events: List<MyEvent>) {

        mEventsAdapter.mAdapterEvents = events
        mEventsAdapter.notifyDataSetChanged()

        eventsView.showEmptyListHint(events.isEmpty())
    }

    override fun updateEvents() {

        mEvents = runBlocking { async { queryEvents() }.await() }

        updateEvents(mEvents)

        eventsView.animSortList()
    }

    // Fot the btm nav view items.
    override fun filterEvents(eventFilter: EventFilter) {
        filter = eventFilter

        //val filteredEvents

        mSubEvents = mEvents.filter {
            when (filter) {
                EventType.UNTIL_EVENTS -> !isSinceEvent(it) // filter and remain those this bool is true
                EventType.SINCE_EVENTS -> isSinceEvent(it)
                EventType.STARRED_EVENTS -> it.isStarred
                EventType.CATEGORY_EVENTS -> it.categoryCode != 5 // tab positon not have 5, but have 0
                else -> it.datetime > 0 // How to express no filter, keep all?
            }
        } as ArrayList<MyEvent>

        updateEvents(mSubEvents)
    }

    override fun sortEvents() {

        val eventsToSort = mEventsAdapter.mAdapterEvents as ArrayList<MyEvent>

        if (eventsToSort.size < 2) return

        //mAscSorting = eventsToSort[0].datetime < eventsToSort[1].datetime

        eventsToSort.apply {
            if (mAscSorting) {
                this.sortByDescending { it.datetime }
            } else {
                this.sortBy { it.datetime }
            }
        }

        mEventsAdapter.notifyDataSetChanged()

        mAscSorting = !mAscSorting
    }

    // Below are funs for ac. ac has a ref for presenter, therefore, no need to override to invoke it.
    fun getSubtitleStr(): String {
        val size = mEventsAdapter.mAdapterEvents.size

        val unitStr = if (size > 1) "events" else "event"

        return "$size $unitStr"
    }
}