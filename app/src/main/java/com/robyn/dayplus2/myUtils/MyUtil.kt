package com.robyn.dayplus2.myUtils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.support.design.widget.Snackbar
import android.view.View
import com.robyn.dayplus2.R

/**
 * Created by yifei on 10/5/2017.
 */

fun makeSnack(view: View, msg: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, msg, duration).show()
}

/**
 * To notify build finish
 */
fun playSound(context: Context) {
    val soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)

    val soundId = soundPool.load(context, R.raw.noti, 1)

    soundPool.play(soundId, 1f, 1f, 0, 2, 1f)

    val mPlayer = MediaPlayer.create(context, R.raw.noti)
    mPlayer.start()
}