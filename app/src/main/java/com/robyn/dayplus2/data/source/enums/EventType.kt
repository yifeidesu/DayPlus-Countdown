package com.robyn.dayplus2.data.source.enums

/**
 * Created by yifei on 11/20/2017.
 */
enum class EventType : EventFilter {
    ALL_EVENTS,
    SINCE_EVENTS,
    UNTIL_EVENTS,
    STARRED_EVENTS,
    CATEGORY_EVENTS;

    fun codeToFilterType(code: Int) {
        when (code) {
            0 -> ALL_EVENTS

        }
    }

}

