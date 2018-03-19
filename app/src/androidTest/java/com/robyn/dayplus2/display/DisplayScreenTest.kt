package com.robyn.dayplus2.display

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource.Companion.getInstance
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayScreenTest {

    val TITLE = "TITLE"

    @Rule
    var rule: ActivityTestRule<DisplayActivity> =
        object : ActivityTestRule<DisplayActivity>(DisplayActivity::class.java) {

            // this is the intent that launches this DisplayActivity.
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                val result = Intent(targetContext, DisplayActivity::class.java)

                result.putExtra(DisplayActivity.EXTRA_EVENT_ID_DISPLAY, "")//todo
                return result
            }
        }

    @Test
    fun checkDisplay_test() {
        onView(withId(R.id.edit)).check(matches(isDisplayed()))
    }



}