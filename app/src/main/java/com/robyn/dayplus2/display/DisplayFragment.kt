package com.robyn.dayplus2.display

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.addEdit.AddEditActivity
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.myUtils.PictureUtils
import com.robyn.dayplus2.myUtils.dayCountAbs
import com.robyn.dayplus2.myUtils.ifSinceStr
import com.robyn.dayplus2.myUtils.makeSnack

/**
 * Created by yifei on 5/15/2017.
 */

class DisplayFragment : Fragment(), DisplayContract.View {

    override lateinit var mPresenter: DisplayContract.Presenter

    lateinit var eventId: String
    lateinit var mEvent: MyEvent

    lateinit var mBgImage: ImageView

    lateinit var mDisplayCallback: DisplayCallback

    interface DisplayCallback {
        fun setToolbarContent(event: MyEvent)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        mDisplayCallback = context as DisplayCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        arguments?.let { eventId = it.getString(ARG_EVENT_ID_DISPLAY) }
        mEvent = mPresenter.fetchEvent(eventId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.display_fg, container, false)

        //mBgImage = view.findViewById(R.id.display_bg_image)

        //mPresenter.setFragmentContent()

        return view
    }

    override fun onResume() {
        super.onResume()

        mPresenter.loadEvent()
        mPresenter.setFragmentContent()

        //mPresenter.updateToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {

            R.id.edit -> { // not calling this
                context?.apply {
                    val intent = AddEditActivity.newIntent(this, eventId)
                    startActivityForResult(intent, REQUEST_CODE_EDIT)
                }
                true
            }

            R.id.remove -> {

                // todo make this dial a snack bar dial
                activity?.apply {
                    AlertDialog
                        .Builder(this)
                        .setMessage("Sure to remove it?")
                        .setPositiveButton("YES", { dialogInterface, _ ->
                            //dialogInterface.dismiss()
                            mPresenter.deleteEvent()

                            val returnIntent = Intent()
                            returnIntent.putExtra("result", 0)
                            setResult(Activity.RESULT_OK, returnIntent)

                            activityFinish()

                            view?.apply { makeSnack(this, "Removed!") }
                        })
                        .setNegativeButton(
                            "NO",
                            { dialogInterface, _ ->
                                dialogInterface.dismiss()

                            })
                        .show()
                }
                item.itemId == R.id.remove
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT) {
            view?.let {
                with(mPresenter) {
                    loadEvent()
                    setFragmentContent()
                    updateToolbar()
                }
            }
        }
    }

    override fun activityFinish() {
        activity?.finish()
    }

    override fun updateToolbar(event: MyEvent) {
        mDisplayCallback.setToolbarContent(event)
    }

    override fun setDisplayContent(event: MyEvent) {
        view?.apply {
            findViewById<TextView>(R.id.title_display_fragment)?.text = event.title
            findViewById<TextView>(R.id.day_count)?.text = event.dayCountAbs().toString()
            findViewById<TextView>(R.id.if_since)?.text = event.ifSinceStr()

            // Setup background image
            val bgImageView = findViewById<ImageView>(R.id.bg_image_display_fg)
            activity?.apply {
                event.bgImagePath?.let { PictureUtils.setImageFileToImageView(this, bgImageView, it) }
            }
        }
    }

    companion object {
        const val ARG_EVENT_ID_DISPLAY = "display_event_id"

        const val REQUEST_CODE_EDIT = 4

        fun newInstance(id: String): DisplayFragment {
            val args = Bundle()
            args.putString(ARG_EVENT_ID_DISPLAY, id)
            val fragment = DisplayFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): DisplayFragment {
            return DisplayFragment()
        }
    }
}

