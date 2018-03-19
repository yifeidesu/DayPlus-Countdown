package com.robyn.dayplus2.display

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.local.EventDatabase
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import com.robyn.dayplus2.myUtils.formatDateStr
import com.robyn.dayplus2.myUtils.getImageFile
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class DisplayActivity : AppCompatActivity(), DisplayFragment.DisplayCallback {

    private lateinit var mEvent: MyEvent // Current event events_item to display

    lateinit var mPresenter: DisplayPresenter

    var mActionBar: ActionBar? = null

    lateinit var mDataSource: EventsLocalDataSource

    override fun setToolbarContent(event: MyEvent) {
        customToolbar(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_ac)

        mDataSource = getDataSource()

//        // Setup toolbar title and subtitle. cannot included in set fg content
//        val initialId = intent.getStringExtra(EXTRA_EVENT_ID_DISPLAY)
//        mEvent = runBlocking { async { mDataSource.fetchEvent(initialId) }.await() }
//        customToolbar(mEvent)

        // Setup PagerAdapter
        val events = runBlocking { async { mDataSource.fetchAllEvents() }.await() }
        val eventsCount = events.size
        val fragmentManager = supportFragmentManager
        val pager = findViewById<ViewPager>(R.id.fg_container_display_ac_pager)

        pager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {

            // Returns the fg that passing to the framework.
            override fun getItem(position: Int): Fragment {

//                val uuid = runBlocking {
//                    async { mDataSource.fetchAllEvents()[position].uuid }.await()
//                }
                val uuid = events[position].uuid

                val fragment = DisplayFragment.newInstance(uuid)
                DisplayPresenter(uuid, mDataSource, fragment)
                return fragment
            }

            override fun getCount(): Int {
                return eventsCount
            }
        }

        val id = intent.getStringExtra(EXTRA_EVENT_ID_DISPLAY)


        val currentPosition = intent.getIntExtra(EXTRA_EVENT_POSITION_DISPLAY, 0)
        val currentEvent = events[currentPosition]

        setSupportActionBar(findViewById(R.id.toolbar_display_ac))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            customToolbar(currentEvent)
        }

        pager.currentItem = currentPosition
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                customToolbar(events[position])
            }
        })

        // todo move to fg
        //val background = findViewById<ImageView>(R.id.background_pic_display_ac)


        //mEvent = this..fetchEvent(id)

//        if (mEvent.getImageFile(applicationContext) != null) {
//            val file = mEvent.getImageFile(applicationContext)
//            file?.let {
//                val uri = FileProvider.getUriForFile(
//                    applicationContext,
//                    "com.robyn.dayplus2.fileprovider",
//                    it
//                )
//                background.setImageURI(uri)
//            }
//        }
//
//        // todo move to fg
//        mEvent.getImageFile(applicationContext)?.apply {
//            val file = this
//            val uri = FileProvider.getUriForFile(applicationContext, AUTHORITY, file)
//            background.setImageURI(uri)
//        }
    }

    private fun getDataSource():EventsLocalDataSource {
        return EventsLocalDataSource
            .getInstance(EventDatabase.getInMemoryDatabase(applicationContext).eventsDao())
    }

    private fun customToolbar(event: MyEvent) {
        supportActionBar?.let {
            it.title = event.title.toString()
            it.subtitle = event.formatDateStr()
        }
    }




    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_display, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

//            R.id.edit -> {
//                startActivity(
//                    AddEditActivity.newIntent(
//                        applicationContext,
//                        intent.getStringExtra(EXTRA_EVENT_ID_DISPLAY)
//                    )
//                )
//
//                return true
//            }
//        // todo move to fg
//            R.id.remove -> {
//                val builder = AlertDialog.Builder(this)
//                builder.setMessage("Sure to remove this mEvent?")
//                    .setPositiveButton("Yes") { dialog, id ->
//                        //dataSource.removeEvent(mEvent.mUuid)
//                        this@DisplayActivity.finish()
//                        val intent = Intent(
//                            applicationContext,
//                            EventsActivity::class.java
//                        )
//                        startActivity(intent)
//                    }
//                    .setNegativeButton("No") { dialog, id -> }
//                builder.create().show()
//                return true
//            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private const val AUTHORITY = "com.robyn.dayplus2.fileprovider"

        const val EXTRA_EVENT_ID_DISPLAY = "com.robyn.dayplus2.event_id"
        const val EXTRA_EVENT_POSITION_DISPLAY = "EXTRA_EVENT_POSITION_DISPLAY"

        fun newIntent(context: Context, id: String): Intent {
            val intent = Intent(context, DisplayActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID_DISPLAY, id)
            return intent
        }
    }
}
