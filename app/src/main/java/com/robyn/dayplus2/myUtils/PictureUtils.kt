package com.robyn.dayplus2.myUtils

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.widget.ImageView
import android.provider.MediaStore



/**
 * Created by yifei on 5/21/2017.
 */

object PictureUtils {
    fun getScaledBitmap(path: String, activity: Activity): Bitmap {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)
        return getScaledBitmap(path, point.x, point.y)
    }

    private fun getScaledBitmap(path: String, destX: Int, destY: Int): Bitmap {

        // get src size
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val srcX = options.outWidth.toFloat()
        val srcY = options.outHeight.toFloat()

        // How much to scale down
        var inSampleSize = 4

        if (srcX > destX || srcY > destY) {
            val scaledX = srcX / destX.toFloat()
            val scaledY = srcY / destY.toFloat()

            inSampleSize = Math.round(if (scaledY > scaledX) scaledY else scaledX)
        }

        options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize


        return BitmapFactory.decodeFile(path, options)
    }
//
//    fun getScaledDrawbale(path: String, activity: Activity): Drawable {
//        val size = Point()
//        activity.windowManager.defaultDisplay
//                .getSize(size)
//        val bitmap = getScaledBitmap(path, size.x, size.y)
//        return BitmapDrawable(activity.resources, bitmap)
//    }

    fun setImageFileToImageView(activity: Activity, imageView: ImageView, imagePath: String) {

        activity.let {
            val bitmap = PictureUtils.getScaledBitmap(imagePath, it)

            imageView.setImageBitmap(bitmap)
        }
    }

    fun getRealPathFromUri(resolver: ContentResolver,contentUri: Uri): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = resolver.query(contentUri, proj, null, null, null)
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            path = cursor.getString(column_index)
        }
        cursor.close()
        return path
    }
}
