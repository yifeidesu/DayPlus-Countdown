package com.robyn.dayplus2.display

import com.robyn.dayplus2.BasePresenter
import com.robyn.dayplus2.BaseView
import com.robyn.dayplus2.data.MyEvent

/**
 * Created by yifei on 11/23/2017.
 */
interface DisplayContract {
    interface View : BaseView<Presenter> {

        fun activityFinish()

        fun setDisplayContent(event: MyEvent)
        fun updateToolbar(event: MyEvent)
    }

    interface Presenter : BasePresenter {

        fun loadEvent()

        fun setFragmentContent()

        fun deleteEvent()
        fun fetchEvent(uuidString: String):MyEvent
        fun getEvent():MyEvent

        //fun updateDisplayContent() // Actionbar + fg content

        fun positionToUuid(position: Int): String

        fun uuidToPosition(uuid: String): Int

        fun getEventsCount(): Int

        fun updateToolbar()
    }
}