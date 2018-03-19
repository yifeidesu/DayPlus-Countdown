package com.robyn.dayplus2.events

import android.content.Context
import android.os.AsyncTask
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.EventsDataSource
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class EventsTask : AsyncTask<EventsDataSource,Void, List<MyEvent>?>(){

    override fun doInBackground(vararg params: EventsDataSource?): List<MyEvent>? {
        val dataSource = params[0]
        return runBlocking { async {  dataSource?.fetchAllEvents() }.await() }//???????????????
    }


    companion object {

    }







}