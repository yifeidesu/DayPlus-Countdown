package com.robyn.dayplus2.addEdit

import android.arch.persistence.room.Room
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.DatePicker
import com.robyn.dayplus2.R
import com.robyn.dayplus2.click
import com.robyn.dayplus2.pickDateOnDatePicker
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.InstrumentationRegistry
import com.robyn.dayplus2.data.MyEvent
import org.junit.Before



/**
 * 7 edit sections in [AddEditFragment]. Those are:
 *  1, background pic
 *  2, date
 *  3, repeat mode
 *  4, title
 *  5, description
 *  6, star
 *  7, category
 */
@RunWith(AndroidJUnit4::class)
class AddEditScreenTest {

    lateinit var mDb: com.robyn.dayplus2.data.source.local.EventDatabase


    @Rule
    @JvmField
    var mRule = ActivityTestRule<AddEditActivity>(AddEditActivity::class.java)

    @Test
    fun titleLayoutIsDisplayed_test() {
        onView(withId(R.id.title_layout)).check(matches(isDisplayed()))
    }

    // click on camera fab, show options in btm sheet
    @Test
    fun clickFab_showBtmSheet() {
        // click on camera fab
        onView(withId(R.id.camera_fab)).perform(click())

        // check btm sheet display, by check one view it contains
        onView(withId(R.id.camera_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCancel_dismissBtmSheet_pickPic() {
        // click on camera fab.
        onView(withId(R.id.camera_fab)).perform(click())

        // if btm sheet displays, click on its cancel btn.
        onView(withId(R.id.cancel_btn_pic)).perform(click())

        // check if the camera btn not exist, which means dial dismissed.
        onView(withId(R.id.camera_btn)).check(doesNotExist())
    }

    @Test
    fun clickRepeatModeOptions_viewUpdates() {

        onView(withId(R.id.repeat_layout_edit)).perform(click())
        onView(withId(R.id.weekly)).perform(click())
        onView(withText(containsString("Week"))).check(matches(isDisplayed()))

        onView(withId(R.id.repeat_layout_edit)).perform(click())
        onView(withId(R.id.monthly)).perform(click())
        onView(withText(containsString("Month"))).check(matches(isDisplayed()))

        onView(withId(R.id.repeat_layout_edit)).perform(click())
        onView(withId(R.id.annually)).perform(click())
        onView(withText(containsString("Year"))).check(matches(isDisplayed()))

        onView(withId(R.id.repeat_layout_edit)).perform(click())
        onView(withId(R.id.never)).perform(click())
        onView(withText(containsString("Never"))).check(matches(isDisplayed()))
    }

    @Test
    fun datePickerPicksDate_showDate() {

        // prepare the date string to check if shows on date picker
        val year = 2018
        val monthOfYear = 3
        val dayOfMonth = 21

        val formatDateStr = formatDateStr(year, monthOfYear, dayOfMonth)

        // perform setting this date on date picker
        pickDateOnDatePicker(R.id.date_layout_edit, year, monthOfYear, dayOfMonth)

        // check if formatted date string is showing on date text view.
        onView(withId(R.id.date_text_view_edit)).check(matches(withText(formatDateStr)))
    }

    /**
     *  returns the format date string the date text view supposed to show.
     */
    private fun formatDateStr(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val fmt = DateTimeFormat.forPattern("dd MMM, yyyy")
        return DateTime(year, monthOfYear, dayOfMonth, 0, 0).toString(fmt)
    }

    /**
     *  perform picking a date on date picker
     */
    private fun pickDateOnDatePicker(
        datePickerTriggerViewId: Int,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        // open the date picker dial
        onView(withId(datePickerTriggerViewId)).perform(click())

        // find date picker by class name, perform picker action from espresso-contrib
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth))

        // dials' ok button id = button1
        onView(withId(android.R.id.button1)).perform(click())
    }

    // Click on category textview, show dial, pick image, dial dismisses, textview shows the picked image
    @Test
    fun pickCategory_showPickedImage() {

        checkCategoryByTag(R.id.cake, R.drawable.ic_action_cake)
        checkCategoryByTag(R.id.loved, R.drawable.ic_action_loved)
        checkCategoryByTag(R.id.face, R.drawable.ic_action_face)
        checkCategoryByTag(R.id.social, R.drawable.ic_action_social)
        checkCategoryByTag(R.id.work, R.drawable.ic_action_work)
    }

    private fun checkCategoryByTag(imageViewId:Int, drawableResInt:Int) {

        click(R.id.category_textview_addedit)
        click(imageViewId)

        onView(withId(imageViewId)).check(doesNotExist())
        onView(withTagValue(equalTo(drawableResInt))).check(matches(isDisplayed()))
    }

    @Test
    fun saveDate() {

        val title = "saveDate"

        val year = 2018
        val monthOfYear = 3
        val dayOfMonth = 12

        // Type in title in title EditText
        onView(withId(R.id.title_edit_text)).perform(typeText(title))

        click(R.id.date_layout_edit)

        // Pick a date on date picker
        pickDateOnDatePicker(year, monthOfYear, dayOfMonth)

        //Espresso.pressBack()
        //val save = mRule.activity.getResources().getString(R.string.save)
        //click(save)

        click(R.id.add_edit_menu_save) // click save to invoke presenter.save()


        val event = mRule.activity.mPresenter.mEvent
        val eventDate = event.datetime // Long
        val givenDate = DateTime(year, monthOfYear, dayOfMonth,0,0,0).millis

        assertThat(eventDate, `is`(givenDate))

        // check if picked date saved on mEvent
    }

}