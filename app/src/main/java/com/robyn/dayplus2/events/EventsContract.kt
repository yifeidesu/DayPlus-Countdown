package com.robyn.dayplus2.events

import com.robyn.dayplus2.BasePresenter
import com.robyn.dayplus2.BaseView
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.enums.EventFilter

/**
 * Created by yifei on 11/18/2017.
 */
interface EventsContract {

    interface View : BaseView<Presenter> {

        fun updateEvents(events: List<MyEvent>)
        fun showFilteredEvents(eventFilter: EventFilter)
        fun showEmptyListHint(isEmptyList: Boolean = true)
        fun setRecyclerAdapter(view: android.view.View, events: List<MyEvent>)

        fun animSortList()
        fun animBtmUpdateList()
    }

    interface Presenter : BasePresenter {

        var eventsAdapter: EventsAdapter
        var filter: EventFilter

        fun bind()
        fun filterEvents(eventFilter: EventFilter)
        fun sortEvents()
        fun updateEvents(events: List<MyEvent>)
        fun updateEvents()
    }
}