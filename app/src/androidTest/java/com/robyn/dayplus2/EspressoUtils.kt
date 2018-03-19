package com.robyn.dayplus2

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.widget.DatePicker
import org.hamcrest.Matchers

fun click(viewResId: Int) {
    Espresso.onView(ViewMatchers.withId(viewResId)).perform(ViewActions.click())
}

fun click(text: String) {
    Espresso.onView(ViewMatchers.withText(text)).perform(ViewActions.click())
}

fun checkDisplayed(idInt:Int) {
    onView(withId(idInt)).check(matches(isDisplayed()))
}

fun checkDisplayed(text: String) {
    onView(withText(text)).check(matches(isDisplayed()))
}

// NOT equivalent to Espresso.pressBack
fun pressUp() {
    Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
        .perform(ViewActions.click())
}



// Pick a date on DatePicker, using espresso's PickerActions
fun pickDateOnDatePicker(
    year: Int,
    monthOfYear: Int,
    dayOfMonth: Int
) {
    Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker::class.java.name)))
        .perform(
            PickerActions.setDate(
                year,
                monthOfYear,
                dayOfMonth
            )
        )

    click(android.R.id.button1)
}