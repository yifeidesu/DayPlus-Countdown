package com.robyn.dayplus2.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.robyn.dayplus2.events.EventsActivity
import com.robyn.dayplus2.myUtils.playSound

/**
 *  for splash screen. bg pic defined in ac theme in manifest
 */

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //playSound(this)

        val intent = Intent(this, EventsActivity::class.java)
        startActivity(intent)
        finish()
    }
}
