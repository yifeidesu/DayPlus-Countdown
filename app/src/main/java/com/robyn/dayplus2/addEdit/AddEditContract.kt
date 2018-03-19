package com.robyn.dayplus2.addEdit

import android.content.Context
import android.support.v7.app.ActionBar
import com.robyn.dayplus2.BasePresenter
import com.robyn.dayplus2.BaseView
import com.robyn.dayplus2.data.MyEvent
import com.robyn.dayplus2.data.source.enums.EventField

/**
 * Created by yifei on 11/14/2017.
 *
 * The contract between the AddEdit view and presenter
 */
interface AddEditContract {

    interface View : BaseView<Presenter> {

        fun setTitle(title: String)
        fun setDescription(description: String?)
        fun setDate(eventDateStr: String)
        fun setRepeatDrawable(repeatCode: Int)
        fun setCategoryDrawable(categoryCode: Int)
        fun setStarColor(isStarred: Boolean)
        fun setPreview(imagePath: String)
    }

    interface Presenter : BasePresenter {

        fun loadData()
        fun saveData()

        fun getImagePath(context: Context): String?
        fun getTheEvent(): MyEvent // todo rewrite
        fun getRepeatCodeFromViewId(viewId: Int): Int
        fun getCategoryCodeFromViewId(viewId: Int): Int

        fun setImagePreview()
        fun setMemberVariable(field: EventField, value: Any?)

        fun toggleStar()
        fun makeDateDial()

        // ac's fun
        fun customToolbar(actionBar: ActionBar, eventId: String?)


    }
}