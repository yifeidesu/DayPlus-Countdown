package com.robyn.dayplus2.events

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.DatePicker
import com.robyn.dayplus2.R
import com.robyn.dayplus2.click
import com.robyn.dayplus2.data.source.local.EventsDao
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import com.robyn.dayplus2.pressUp
import kotlinx.coroutines.experimental.launch
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsScreenTest {

    lateinit var mDb: com.robyn.dayplus2.data.source.local.EventDatabase

    @Rule
    @JvmField
    var rule = object : ActivityTestRule<EventsActivity>(EventsActivity::class.java) {

        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            // Doing this in @Before generates a race condition.

            val context = InstrumentationRegistry.getTargetContext()

            mDb = Room.inMemoryDatabaseBuilder(
                context,
                com.robyn.dayplus2.data.source.local.EventDatabase::class.java
            ).build()

            val mDao = mDb.eventsDao()

            launch { deleteAllEvents(mDao) }
        }
    }

    private fun deleteAllEvents(mDao: EventsDao) {
        EventsLocalDataSource.getInstance(mDao).deleteAllEvents()
    }

//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        mDb.close()
//    }

    @Test
    fun createMenuItem_test() {
        onView(withId(R.id.create_events_menu)).perform(click())

        onView(withId(R.id.add_edit_fg_container)).check(matches(isDisplayed()))
    }

    // db no open error
    @Test
    fun clickOnRecyclerViewItem_test() {
        // list　-> edit -> list

        val title = "test1"

        createEventForTest(title)

        pressUp()

        onView(withText(title)).perform(click())
        onView(withId(R.id.toolbar_display_ac)).check(matches(isDisplayed()))
    }

    // create, back to list, click new created events_item, display screen shows the title.
    @Test
    fun clickCreate_pressUp_clickItem_displayShowTitle() {

        val title = "test5"

        createEventForTest_pressUp(title)

        onView(withText(title)).perform(click())

        onView(withId(R.id.title_display_fragment)).check(matches(withText(title)))
    }

    @Test
    fun createTitleEmptyEvent_cannotSave() {

        // create a normal event events_item
        createEventForTest_pressUp("testEmptyTitle")

        // try creating an event with empty title
        createEventForTest_pressUp("")

        // check no event with empty title in the list is displayed
        onView(allOf(withId(R.id.title_item), withText(""))).check(doesNotExist())
    }

    @Test
    fun clickSort_listOrderChanged() {
        val title0 = "title0"
        val title1 = "title1"

        createEventForTest_pressUp(title0)
        createEventForTest_pressUp(title1)

        // click sort button
        onView(withId(R.id.sort_events_menu)).perform(click())

        onView(withText(title1)).check(matches(isDisplayed()))

        // check in recylerview at position 0, title is the 2nd input one.
//        onData(withText(title1))
//            .inAdapterView(withId(R.id.recycler_list))
//            .atPosition(0)
//            .check(matches(withText(title1)))

//        onData(anything())
//            .inAdapterView(withId(R.id.recycler_list))
//            .atPosition(0)
//            .onChildView(withId(R.id.title_item))
//            .check(matches(withText(containsString(title1))))
//
//        onData(`is`(instanceOf(RecyclerView::class.java)))
//            .atPosition(0)
//            .onChildView(withId(R.id.title_item)).check(matches(withText(title1)))
    }

    @Test
    fun clickBtmTab_updateList() {

        createEventForTest_pressUp("testBtmTabs")
        createEventForTest("testStar")
        onView(withId(R.id.star_textview_addedit)).perform(scrollTo(), click())
        pressUp()

        createEventWithCategory()

        click(R.id.star_btm)
        //onView((withId(R.id.star_item))).check(matches(isDisplayed()))

        click(R.id.category_btm)

        // every in the list contains this string.
        //onView((withId(R.id.date_item),)).check(matches(withText(containsString("Since"))))

        //onView(withId(R.id.recycler_list)).atPosition(0, withId(R.id.category_item)).check(matches(isDisplayed()))

        //onView(withId(R.id.recycler_list)).check(matches(atPosition(0, withId(R.id.category_item))))
    }

    // create an event events_item with category, back to events screen, category image is displayed.
    @Test
    fun createEventWithCategory_categoryImageDisplayedTest() {
        val title = "test_category"

        createEventForTest(title)

        click(R.id.category_textview_addedit)

        click(R.id.face)

        pressUp()

        onView(withTagValue(equalTo(R.drawable.ic_action_face))).check(matches(isDisplayed()))
    }

    // without editing title, the other editted fields will not be saved on reenter the addedit screen.
    @Test
    fun noTitleStar_test() {
        click(R.id.create_events_menu)

        click(R.id.star_textview_addedit)

        pressUp()

        click(R.id.create_events_menu)

        //onView(withTagValue(equalTo(R.color.star_yellow))).check(matches(isDisplayed()))
    }

    private fun createEventWithCategory() {
        createEventForTest("with category")

        click(R.id.category_textview_addedit)

        click(R.id.face)

        pressUp()
    }

    private fun createEventForTest(titleInput: String) {
        // create an event with titleInput

        onView(withId(R.id.create_events_menu)).perform(click())
        onView(withId(R.id.title_edit_text)).perform(typeText(titleInput))
    }

    private fun createEventForTest_pressUp(title: String) {

        createEventForTest(title)

        pressUp()
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        //  checkNotNull(itemMatcher)
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has events_item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                        ?: // has no events_item on such position
                        return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    /**
     * Test delete function
     *
     * steps:
     *
     * click item to enter detail view ->
     * click remove icon to remove and finish activity ->
     * check on list, if the removed item exists.
     */
    @Test
    fun deleteEvent_notDisplayInList() {

        // Create an item
        val title = "testTitle"
        createEventForTest(title)

        // Press up to go back to events view
        pressUp()

        // Click this item to enter its detail view
        click(title)

        // In detail view, click on remove icon
        click(R.id.remove)

        // Check if this item exists
        onView(withText(title)).check(doesNotExist())
    }

    @Test
    fun daysCount_showingInItem() {
        val title = "daysCount"

        val year = 2018
        val monthOfYear = 3
        val dayOfMonth = 12

        createEventForTest(title)

        click(R.id.date_layout_edit)

        // Pick a date in date picker, with espresso's PickerActions
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                monthOfYear,
                dayOfMonth
            )
        )

//        // todo set presenter to current addedit ac
//
//        click(android.R.id.button1)
//
//        pressUp()
//
//        onView(withText(3)).check(matches(isDisplayed()))
    }

    // Create an event with a title. Return to list. Check if this title is displayed.
    @Test
    fun createEvent_titleDisplayed() {
        val title = "testCreate"
        createEventForTest_pressUp(title)

        onView(withText(title)).check(matches(isDisplayed()))
    }

    // Create an event with a title, and category. Return to list. Check if the category is displayed.
    @Test
    fun createEvent_categoryIconDisplayed() {
        val title = "CategoryIconTest"
        createEventForTest(title)

        // Click category TextView to show BottomSheet
        click(R.id.category_textview_addedit)

        // Select cake icon
        click(R.id.cake)

        // Return to list
        pressUp()

        // Check if cake icon displayed in list
        onView(withContentDescription(R.id.cake)).check(matches(isDisplayed())) // todo with content or tag?!
    }

    // Check if actionbar subtitle updates as creating new events
    @Test
    fun actionBarSubTitleUpdate_afterCreate() {

        // Create an event for testing
        createEventForTest_pressUp("test1")

        // Check actionbar subtitle update to 1
        onView(
            allOf(
                withText(containsString("1")),
                isDescendantOfA(withId(R.id.toolbar_list_content))
            )
        )
            .check(matches(isDisplayed()))

        // Create another event for testing
        createEventForTest_pressUp("test2")

        // Check actionbar subtitle update to 2
        onView(
            allOf(
                withText(containsString("2")),
                isDescendantOfA(withId(R.id.toolbar_list_content))
            )
        )
            .check(matches(isDisplayed()))
    }

    // events -> display -> edit -> display -> events. 5 screens
    @Test
    fun clickItem_clickEdit_edit_upShowUpdate_upShFowUpdate() {
        val title = "title"
        val titleEdit = "titleEdit"

        createEventForTest_pressUp(title)

        click(title)

        // In display activity, press edit menu events_item.
        click(R.id.edit)

        // In addEditActivity, edit title.
        onView(withId(R.id.title_edit_text)).perform(clearText(), typeText(titleEdit))

        // Press save icon
        //click(R.id.add_edit_menu_save) // press up or save
        // Return to display screen
        pressUp()

        // Check if title updates on display screen
        onView(withText(titleEdit)).check(matches(isDisplayed()))
    }

    @Test fun create_edit_infoDisplayed() {

        // Create item
        val title = "create_edit"
        createEventForTest(title)

        // Set starred
        click(R.id.star_textview_addedit)

        // Set category
        click(R.id.category_textview_addedit)
        onView(withTagKey(R.drawable.ic_action_face)).perform(click())

        // Press up to save and return to list screen
        pressUp()
        // click on just created item
        click(title)

        // Click MenuItem "edit"
        click(R.id.edit)

        // Check category displayed
        onView(withTagKey(R.drawable.ic_action_face)).check(matches(isDisplayed()))
    }
}