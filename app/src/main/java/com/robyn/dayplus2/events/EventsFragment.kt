package com.robyn.dayplus2.events

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ScrollView
import android.widget.TextView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.addEdit.AddEditActivity
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.enums.EventFilter
import com.robyn.dayplus2.myUtils.categoryCodeToFilter

/**
 *
 * Display a list of [MyEvent]s
 *
 * Created by yifei on 5/11/2017.
 */

class EventsFragment : Fragment(), EventsContract.View {

    override lateinit var mPresenter: EventsContract.Presenter

    lateinit var mRecyclerView: RecyclerView
    lateinit var mScrollView: ScrollView

    lateinit var mOnEventsUpdateListener: OnEventsUpdateListener

    interface OnEventsUpdateListener {
        fun updateActionbarSubtitle()
    }

    override fun onResume() {
        super.onResume()

        mPresenter.updateEvents()
        mOnEventsUpdateListener.updateActionbarSubtitle()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // Cast hosting activity to OnEventsUpdateListener,
        // to invoke updateActionbarSubtitle() when updates list
        try {
            mOnEventsUpdateListener = context as OnEventsUpdateListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnEventsUpdateListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.events_fg, container, false)

        setHasOptionsMenu(true)

        mScrollView = view.findViewById(R.id.scroll_view)
        mRecyclerView = view.findViewById(R.id.recycler_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = mPresenter.eventsAdapter


        setupMinorFilterOptions(view)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.create_events_menu -> {
                context?.apply {

                    val intent = AddEditActivity.newIntent(this)
                    startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT)
                }
                return true
            }

            R.id.sort_events_menu -> {
                //resortList(mSortAsc)

                mPresenter.sortEvents()

                //mPresenter.sortEvents()

                //mScrollView.smoothScrollTo(0,0)

                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != RESULT_OK) return

//        when (requestCode) {
//            REQUEST_CODE_CREATE_EVENT ->
//                mPresenter.updateEvents() // todo notify change for the 1st events_item
//
//            REQUEST_CODE_EDIT_EVENT ->
//
//        }

        mPresenter.updateEvents()


        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showFilteredEvents(eventFilter: EventFilter) {
        mPresenter.filterEvents(eventFilter)
    }

    override fun updateEvents(events: List<MyEvent>) {
    }

    /**
     * When current list is empty, that is [isEmptyList] is true, show list empty hint;
     * or when current list is no longer empty, that is [isEmptyList] is false, turn off the list empty hint
     */
    override fun showEmptyListHint(isEmptyList: Boolean) {
        view?.findViewById<TextView>(R.id.create_one)?.visibility = if (isEmptyList) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun setRecyclerAdapter(view: View, events: List<MyEvent>) {
        mRecyclerView = view.findViewById(R.id.recycler_list) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = mPresenter.eventsAdapter
    }

    override fun animSortList() {
        mScrollView.animation = AnimationUtils.makeInAnimation(context, true)
    }

    override fun animBtmUpdateList() {
        mScrollView.animation = AnimationUtils.makeInAnimation(context, true)
    }

    private fun setupMinorFilterOptions(view: View) {
        view.findViewById<TabLayout>(R.id.tab_layout)
            .addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //resortList(mSortAsc)
                    mPresenter.sortEvents()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab == null) return

                    // tab postion -> filter name
                    updateUIWithCategoryCode(tab.position)
                }
            })
    }

    // todo resort list is a fun in ac, b/c all fgs in the pager will use it.
    private fun resortList(isAsc: Boolean) {
        mPresenter.sortEvents()


        view?.findViewById<ScrollView>(R.id.scroll_view)?.animation =
                AnimationUtils.loadAnimation(context, R.anim.alpha_in)
    }

    /**
     * @param categoryCode filter list with [categoryCode]
     */
//    private fun updateUIWithCategoryCode(eventCategory: EventCategory) {
//////        mAdapterEvents = mPool.fetchEventsByCategory(categoryCode)
////        mAdapterEvents = mPresenter.queryEvents(EventType.CATEGORY_EVENTS)
////        updateUI(mAdapterEvents)
//        mPresenter.filterEvents(eventCategory)
//    }

    private fun updateUIWithCategoryCode(eventCategoryCode: Int) {
        mPresenter.filterEvents(categoryCodeToFilter(eventCategoryCode))

        animSortList() // ?
    }

    companion object {
        private const val PRESENTER_RETRIEVE_KEY = "PRESENTER_RETRIEVE_KEY"

        const val REQUEST_CODE_CREATE_EVENT = 0
        const val REQUEST_CODE_EDIT_EVENT: Int = 1

        private val ARG_SECTION_NUM = "section_num"
        fun newInstance(sectionNumber: Int = 2): EventsFragment {
            val args = Bundle()
            args.putInt(ARG_SECTION_NUM, sectionNumber)
            val fragment = EventsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}


