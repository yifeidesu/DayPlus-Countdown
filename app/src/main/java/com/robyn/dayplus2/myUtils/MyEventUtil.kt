package com.robyn.dayplus2.myUtils

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.robyn.dayplus2.R
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

fun formatDateStr(datetime: Long): String {
    val fmt = DateTimeFormat.forPattern("dd MMM, yyyy")
    return DateTime(datetime).toString(fmt)
}

// When star image is textview's compound drawable.
fun setStarColor(context: Context, textView: TextView, isStar: Boolean) {

    val colorInt =
        if (isStar) (ContextCompat.getColor(context, R.color.star_yellow))
        else (ContextCompat.getColor(context, R.color.grey))

    textView.compoundDrawables[0]?.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
}

fun categoryCodeToDrawableInt(categoryCode: Int): Int? {
    return when (categoryCode) {
        0 -> R.drawable.ic_action_cake
        1 -> R.drawable.ic_action_loved
        2 -> R.drawable.ic_action_face
        3 -> R.drawable.ic_action_social
        4 -> R.drawable.ic_action_work
        else -> null
    }
}

/**
 * In EventsFragment, set category ImageView's drawable.
 * Set tag to imageView for ui test.
 */
fun setCategoryDrawable(context: Context, imageView: ImageView, categoryCode: Int) {

    with(imageView) {

        val drawableInt = categoryCodeToDrawableInt(categoryCode)

        // Set ImageView's tag to drawable res int for ui test
        tag = drawableInt

        drawableInt?.let {
            val drawable = ContextCompat.getDrawable(context, it)
            setImageDrawable(drawable)
        }
    }
}

/**
 * In AddEditFragment, set category TestView's drawableLeft.
 *
 *
 */
fun setCategoryDrawable(textView: TextView, categoryCode: Int) {

    val drawableInt = categoryCodeToDrawableInt(categoryCode)

    drawableInt?.let {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            drawableInt, 0,
            R.drawable.ic_action_right, 0
        )

        textView.tag = it
    }
}


