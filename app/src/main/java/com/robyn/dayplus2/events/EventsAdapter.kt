package com.robyn.dayplus2.events

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.display.DisplayActivity
import com.robyn.dayplus2.display.DisplayActivity.Companion.EXTRA_EVENT_POSITION_DISPLAY
import com.robyn.dayplus2.myUtils.dayCountAbs
import com.robyn.dayplus2.myUtils.formatDateStr
import com.robyn.dayplus2.myUtils.ifSince
import com.robyn.dayplus2.myUtils.setCategoryDrawable

/**
 * Created by yifei on 10/4/2017.
 */

class EventsAdapter(var mAdapterEvents: List<MyEvent>) :
    RecyclerView.Adapter<EventsAdapter.EventHolder>() {

    inner class EventHolder(
        inflater: LayoutInflater, parent: ViewGroup, attach: Boolean
    ) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.events_item, parent, false)),
        View.OnClickListener {

        lateinit var mEvent: MyEvent

        init {
            itemView.setOnClickListener(this)
        }

        internal fun bind(event: MyEvent) {
            mEvent = event

            with(itemView) {

                findViewById<TextView>(R.id.title_item).text = mEvent.title
                findViewById<TextView>(R.id.date_item).text = getSinceDateStr()

                val daysCount = mEvent.dayCountAbs()
                findViewById<TextView>(R.id.days_num).text = daysCount.toString()
                findViewById<TextView>(R.id.days_unit_item).text =
                        if (daysCount > 1) {
                            "Days"
                        } else {
                            "Day"
                        }

                findViewById<ImageView>(R.id.star_ic_item).visibility =
                        if (mEvent.isStarred) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                val categoryImageView = findViewById<ImageView>(R.id.category_ic_item)
                categoryImageView.visibility =
                        if (mEvent.categoryCode != 5) {
                            setCategoryDrawable(
                                this.context,
                                categoryImageView,
                                mEvent.categoryCode
                            )
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
            }
        }

        override fun onClick(v: View) {

            val eventsActivity = itemView.context as FragmentActivity
            val intent = Intent(itemView.context, DisplayActivity::class.java)
            val eventId: String = mEvent.uuid
            val position: Int = mAdapterEvents.indexOf(mEvent)
            intent.putExtra(DisplayActivity.EXTRA_EVENT_ID_DISPLAY, eventId)
            intent.putExtra(EXTRA_EVENT_POSITION_DISPLAY, position)// todo update event at position
            eventsActivity.startActivityForResult(intent, EventsActivity.REQUEST_CODE_DISPLAY_AC)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EventHolder(inflater, parent, true)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.bind(mAdapterEvents[position])
    }

    override fun getItemCount(): Int {
        return mAdapterEvents.size
    }

    private fun EventHolder.getSinceDateStr(): String {
        val ifSince = itemView.resources.getString(mEvent.ifSince())
        val date = mEvent.formatDateStr()
        return ifSince + date
    }
}