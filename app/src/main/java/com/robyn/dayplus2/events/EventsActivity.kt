package com.robyn.dayplus2.events

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.robyn.dayplus2.BuildConfig
import com.robyn.dayplus2.R
import com.robyn.dayplus2.addEdit.AddEditActivity
import com.robyn.dayplus2.data.source.enums.EventType
import com.robyn.dayplus2.data.source.local.EventDatabase
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import com.robyn.dayplus2.events.EventsFragment.Companion.REQUEST_CODE_CREATE_EVENT
import com.robyn.dayplus2.myUtils.JobHolder
import com.robyn.dayplus2.myUtils.addFragment
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * To hold the [EventsFragment]
 *
 */
class EventsActivity : AppCompatActivity(), JobHolder,
    NavigationView.OnNavigationItemSelectedListener, EventsFragment.OnEventsUpdateListener {

    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mPresenter: EventsPresenter
    lateinit var dataSource: EventsLocalDataSource

    lateinit var mToolbar: Toolbar

    override fun updateActionbarSubtitle() {
        setToolbarSubtitle()
    }

    override var job: Job = Job()

    override fun onDestroy() {
        super.onDestroy()

        job.cancel() // cancel all coroutine on this activity's destroy
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_ac)

        setupBtmNavOptions()

        // Setup fragment and presenter
        val eventsFragment =
            supportFragmentManager.findFragmentById(R.id.fg_container_events_ac) as EventsFragment?
                    ?: EventsFragment.newInstance()
                        .also {
                            addFragment(R.id.fg_container_events_ac, it)
                        }

        dataSource = EventsLocalDataSource
            .getInstance(EventDatabase.getInMemoryDatabase(applicationContext).eventsDao())
        mPresenter = EventsPresenter(dataSource, eventsFragment)

        // Setup toolbar
        mToolbar = findViewById(R.id.toolbar_list_content)
        mToolbar.apply {
            setSupportActionBar(this)
        }

        setToolbarSubtitle()

        // Setup drawer toggle on the toolbar
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, mDrawerLayout, mToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Setup in-drawer navigation view
        findViewById<NavigationView>(R.id.drawer_nav_view).setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) //as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     *  For the drawer_nav_view
     *
     *  menu inflated in xml
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.create_drawer -> {

                mDrawerLayout.closeDrawer(GravityCompat.START, false)

                val intent = Intent(this, AddEditActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.list_drawer -> {
                mDrawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.feedback_drawer -> {
                mDrawerLayout.closeDrawer(GravityCompat.START)
                sendFeedback()
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_CODE_CREATE_EVENT -> {
                job = launch(UI, CoroutineStart.UNDISPATCHED)
                { mPresenter.updateEvents() }
            }

            REQUEST_CODE_DISPLAY_AC -> {
                // todo back from display ac, only when delete update

                if (data != null) {
                    return
                } else {
                    val resultdata = data?.getIntExtra("result", 1)
                    if (resultdata == 0) {
                        job = launch(UI, CoroutineStart.UNDISPATCHED)
                        { mPresenter.updateEvents() }
                    }
                }
            }
        }
    }

    private fun setToolbarSubtitle() {
        mToolbar.subtitle = mPresenter.getSubtitleStr()
    }

    /**
     * Not using data at all. Using plain strings and implicit intent.
     * todo move to utils
     */
    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback.dayplus@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to DayPlus")

        val androidVersion = "Android Version: " + Build.VERSION.SDK_INT.toString() + "\n"
        val manufacturer = "Manufacturer: " + Build.MANUFACTURER + "\n"
        val model = "Model: " + Build.MODEL + "\n"
        val version = "DayPlus Version: " + BuildConfig.VERSION_NAME + "\n\n"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            androidVersion + manufacturer + model + version
                    + "======\n"
        )
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }

    // Setup BottomNavigationView
    private fun setupBtmNavOptions() {
        findViewById<BottomNavigationView>(R.id.btm_nav_events).apply {

            menu.findItem(R.id.all_btm).isChecked = true

            setOnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.all_btm -> mPresenter.filterEvents(EventType.ALL_EVENTS)
                    R.id.dayplus_btm -> mPresenter.filterEvents(EventType.SINCE_EVENTS)
                    R.id.dayminus_btm -> mPresenter.filterEvents(EventType.UNTIL_EVENTS)
                    R.id.star_btm -> mPresenter.filterEvents(EventType.STARRED_EVENTS)
                    R.id.category_btm -> mPresenter.filterEvents(EventType.CATEGORY_EVENTS)
                }

                setToolbarSubtitle()

                //setCategoryTabLayoutVisibility(events_item) // when swithch to categoty tab, it appears

                true
            }

            setOnNavigationItemReselectedListener { resortList() }
        }
    }

    private fun resortList() {
        mPresenter.sortEvents()
    }

    companion object {
        private val EXTRA_EVENTS_FILTER = "extra_events_filter"
        val REQUEST_CODE_DISPLAY_AC = 1

        /**
         * When start [EventsActivity], pass in a [filterCode] to indicate how to filter the mAdapterEvents
         */
        fun newIntent(context: Context, filterCode: Int): Intent {
            val intent = Intent(context, EventsActivity::class.java)
            intent.putExtra(EXTRA_EVENTS_FILTER, filterCode)
            return intent
        }
    }
}
