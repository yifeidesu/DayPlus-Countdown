package com.robyn.dayplus2.addEdit

import android.app.DatePickerDialog
import android.content.Context
import android.support.v7.app.ActionBar
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.EventsDataSource
import com.robyn.dayplus2.data.source.enums.EventField
import com.robyn.dayplus2.myUtils.JobHolder
import com.robyn.dayplus2.myUtils.formatDateStr
import com.robyn.dayplus2.myUtils.getImagePath
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.joda.time.DateTime

/**
 * Created by yifei on 11/14/2017.
 *
 * Listen to the user actions from ui [AddEditFragment]
 *
 * [eventId] is the mUuid of the [MyEvent] that is being edited;
 *  if the mEvent is new, mUuid should be created in [AddEditActivity]
 *
 * [view] the addEdit view
 *
 * [dataSource] the source of mEvents data
 *
 */

class AddEditPresenter(
    private val eventId: String?,
    private val view: AddEditContract.View,
    private val dataSource: EventsDataSource
) :
    AddEditContract.Presenter,
    JobHolder,
    EventsDataSource.GetEventCallback {

    var mEvent: MyEvent // open for test
    private var mUuid: String
    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mDatetime: Long = 0
    private var mRepeatMode = 0 // 0 = never
    private var mImagePath: String? = null
    private var mStarred = false
    private var mCategoryCode = 5

    override var job: Job = Job()

    init {
        view.mPresenter = this

        // When create a MyEvent, eventId == null,
        // while mUuid is assigned a value when creating the new mEvent.

        mEvent = if (eventId == null) {
            MyEvent()
        } else {
            runBlocking { async { dataSource.fetchEvent(eventId) }.await() }
        }

        mUuid = mEvent.uuid

        setMemberVariables()
    }

    override fun setMemberVariable(field: EventField, value: Any?) {
        when (field) {
            EventField.EVENT_TITLE -> mTitle = value.toString()
            EventField.EVENT_DESCRIPTION -> mDescription = value.toString()
            EventField.EVENT_IMAGE_PATH -> mEvent.bgImagePath = value.toString()
            EventField.EVENT_DATETIME -> mDatetime = value.toString().toLong()
            EventField.EVENT_REPEAT_MODE -> mRepeatMode = value.toString().toInt()
            EventField.EVENT_STAR -> mStarred = value.toString().toBoolean()
            EventField.EVENT_CATEGORY -> mCategoryCode = value.toString().toInt()
        }
    }

    private fun setMemberVariables() {
        with(mEvent) {
            mTitle = title
            mDescription = description
            mImagePath = bgImagePath
            mDatetime = datetime
            mRepeatMode = repeatMode
            mCategoryCode = categoryCode
            mStarred = isStarred
        }
    }

    // [MyEvent] refer to type and default values in model class.
    private fun setEventProperties() {
        with(mEvent) {
            title = mTitle
            description = mDescription
            bgImagePath = mImagePath
            datetime = mDatetime
            repeatMode = mRepeatMode
            categoryCode = mCategoryCode
            isStarred = mStarred
        }
    }

    // invoked when press up or save icon
    override fun saveData() {

        launch {
            with(mEvent) {

                if (mTitle.isNullOrEmpty()) {

                    dataSource.deleteEvent(this)
                } else {
                    setEventProperties()
                    dataSource.insertEvent(this)
                }
            }
        }
    }

    override fun loadData() {

        setMemberVariables()
        loadDataToView()
    }

    // Set presenter's member variables data to view
    private fun loadDataToView() {
        mImagePath?.let { view.setPreview(it) }
        mTitle?.let { view.setTitle(it) }
        mDescription?.let { view.setDescription(it) }
        view.setDate(formatDateStr(mDatetime))
        view.setRepeatDrawable(mRepeatMode)
        view.setStarColor(mStarred)
        view.setCategoryDrawable(mCategoryCode)
    }

    override fun getRepeatCodeFromViewId(viewId: Int): Int {
        return when (viewId) {
            R.id.never -> 0
            R.id.weekly -> 1
            R.id.monthly -> 2
            R.id.annually -> 3
            else -> 0
        }
    }

    override fun getCategoryCodeFromViewId(viewId: Int): Int {
        return when (viewId) {
            R.id.cake -> 0
            R.id.loved -> 1
            R.id.face -> 2
            R.id.social -> 3
            R.id.work -> 4
            else -> 5
        }
    }

    override fun setImagePreview() {
        mImagePath?.let { view.setPreview(it) }
    }

    override fun getImagePath(context: Context): String? {
        return mEvent.getImagePath(context)
    }

    // todo Getter of the mEvent property. Not a query to db.
    override fun getTheEvent(): MyEvent {
        return mEvent
    }

    override fun toggleStar() {
        mStarred = !mStarred
        setMemberVariable(EventField.EVENT_STAR, mStarred)
        view.setStarColor(mStarred)
    }

    /**
     * Make a date picker dial to show, for the date_layout onClick listener.
     *
     * DatePickerDialog() param month is in 0..11
     * Jada DateTime() param monthOfYear is in 1..12
     * So that, DateTime's monthOfYear = DatePickerDialog()'s month + 1
     */
    override fun makeDateDial() {

        val onDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val pickedDateTimeMillis = DateTime(
                    year, month + 1, dayOfMonth,
                    0, 0
                ).millis

                // update mDatetime
                setMemberVariable(EventField.EVENT_DATETIME, pickedDateTimeMillis)

                val dateStr = formatDateStr(mDatetime)
                view.setDate(dateStr)
            }

        val dateTime = DateTime(mEvent.datetime)
        val year: Int = dateTime.year
        val monthOfYear = dateTime.monthOfYear // DateTime() param monthOfYear in 1..12
        val dayOfMonth = dateTime.dayOfMonth

        val context = (view as AddEditFragment).context

        val dateDial =
            DatePickerDialog(context, onDateSetListener, year, monthOfYear - 1, dayOfMonth)

        dateDial.show()
    }

    // todo callback to update subtitle
    override fun customToolbar(actionBar: ActionBar, eventId: String?) {
        actionBar.setTitle(
            if (eventId == null) (R.string.create_an_event)
            else (R.string.edit_event_title)
        )

    }
}
