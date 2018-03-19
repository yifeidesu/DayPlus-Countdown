package com.robyn.dayplus2.data.source.enums

/**
 * Enum [MyEvent]'s category values.
 *
 * Implements [EventFilter] so that [MyEvent].category can act as filter when filter the list
 *
 * Created by yifei on 11/20/2017.
 */
enum class EventCategory : EventFilter {

    CAKE_EVENTS,

    LOVED_EVENTS,

    FACE_EVENTS,

    EXPLORE_EVENTS,

    WORK_EVENTS
}