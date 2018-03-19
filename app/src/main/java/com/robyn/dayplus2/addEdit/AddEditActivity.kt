package com.robyn.dayplus2.addEdit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.source.enums.EventField
import com.robyn.dayplus2.data.source.local.EventDatabase
import com.robyn.dayplus2.data.source.local.EventsLocalDataSource
import com.robyn.dayplus2.myUtils.addFragment

class AddEditActivity : AppCompatActivity() {

    private lateinit var mActionBar: ActionBar
    lateinit var mPresenter: AddEditPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_ac)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        // Setup fragment and mPresenter
        val addEditFragment =
            supportFragmentManager.findFragmentById(R.id.add_edit_fg_container) as AddEditFragment?
                    ?: AddEditFragment.newInstance(eventId).also {
                        addFragment(R.id.add_edit_fg_container, it)
                    }

        val dataSource =
            EventsLocalDataSource.getInstance(EventDatabase.getInMemoryDatabase(applicationContext).eventsDao())
        mPresenter = AddEditPresenter(eventId, addEditFragment, dataSource)

        // Setup actionBar
        setSupportActionBar(findViewById(R.id.toolbar_add_edit))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)

            mPresenter.customToolbar(this, eventId)
        }

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }


    override fun onDestroy() {
        super.onDestroy()

        mPresenter.job.cancel()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode != Activity.RESULT_OK) return
//
//        if (requestCode == REQUEST_GALLERY) {
//            val imagePath = ""
//            mPresenter.setMemberVariable(EventField.EVENT_IMAGE_PATH, imagePath)
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {

        mPresenter.saveData()
        this.finish()
        setResult(Activity.RESULT_OK)

        return true
    }

    companion object {

        private const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"

        const val REQUEST_CAMERA = 0
        const val REQUEST_GALLERY = 1

        const val KEY_IMAGE_DATA = "KEY_IMAGE_DATA"

        fun newIntent(applicationContext: Context, eventId: String? = null): Intent {
            val intent = Intent(applicationContext, AddEditActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            return intent
        }
    }
}
