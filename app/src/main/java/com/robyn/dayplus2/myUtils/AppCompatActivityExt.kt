package com.robyn.dayplus2.myUtils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

/**
 * Created by yifei on 11/24/2017.
 *
 * [action] is a [FragmentTransaction] void method, e.g. add(), replace(), etc.
 *
 * The [AppCompatActivity] calling this method will have its supportFragmentManager
 * begin the fragment transaction, perform the [action] and commit it.
 */

fun AppCompatActivity.addFragment(@IdRes containerResId: Int, fragment: Fragment) {
    supportFragmentManager.transActCommit {
        add(containerResId, fragment)
        //addToBackStack("fg")
    }
}

fun AppCompatActivity.replaceFragment(@IdRes containerResId: Int, fragment: Fragment) {
    supportFragmentManager.transActCommit { replace(containerResId, fragment) }
}

fun FragmentManager.transActCommit(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply(action).commit()
}


