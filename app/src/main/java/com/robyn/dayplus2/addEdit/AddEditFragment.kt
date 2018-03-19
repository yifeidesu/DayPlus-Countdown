package com.robyn.dayplus2.addEdit

import android.annotation.TargetApi
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.robyn.dayplus2.R
import com.robyn.dayplus2.data.source.enums.EventField
import com.robyn.dayplus2.myUtils.*
import com.robyn.dayplus2.myUtils.PictureUtils.getRealPathFromUri
import com.robyn.dayplus2.myUtils.PictureUtils.setImageFileToImageView
import java.io.File
import java.io.FileNotFoundException

/**
 * The edit screen
 */

class AddEditFragment : Fragment(), AddEditContract.View, View.OnClickListener {

    override lateinit var mPresenter: AddEditContract.Presenter

    private lateinit var mTitle: TextInputEditText
    private lateinit var mDescription: TextInputEditText
    private lateinit var mDate: TextView
    private lateinit var mRepeatMode: TextView
    private lateinit var mStarTextView: TextView
    private lateinit var mCategoryTextView: TextView
    private lateinit var mImagePreview: ImageView

    private lateinit var mBtmSheet_RepeatMode: BottomSheetDialog
    private lateinit var mBtmSheet_PickPic: BottomSheetDialog
    private lateinit var mBtmSheet_Category: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.add_edit_fg, container, false)

        // init view references
        mTitle = view.findViewById(R.id.title_edit_text)
        mDescription = view.findViewById(R.id.description_edit_text)
        mDate = view.findViewById(R.id.date_text_view_edit)
        mRepeatMode = view.findViewById(R.id.repeat_text_view_edit)
        mStarTextView = view.findViewById(R.id.star_textview_addedit)
        mCategoryTextView = view.findViewById(R.id.category_textview_addedit)
        mImagePreview = view.findViewById(R.id.bg_pic_preview)

        // set Listeners
        view.findViewById<FloatingActionButton>(R.id.camera_fab).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.date_layout_edit).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.repeat_layout_edit).setOnClickListener(this)

        mCategoryTextView.setOnClickListener(this)
        mStarTextView.setOnClickListener(this)

        setTitleChangeListener(mTitle)
        setDescriptionChangeListener(mDescription)

        return view
    }

    override fun onResume() {
        super.onResume()

        mPresenter.loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_DATE -> {
                if (data == null) return
//                val date = data
//                        .getSerializableExtra(DatePickerFragment.EXTRA_DATETIME) as DateTime
//                //mEvent.datetime = date.millis
//                mPresenter.setDateTime(date.millis)
            }

            REQUEST_GALLERY -> {

//                try {
//
//
//
//                    //val imageStream = context?.contentResolver?.openInputStream(imageUri)
//                    //val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
//                    //mImagePreview.setImageBitmap(selectedImage)
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                }

                val imageUri = data?.data
                val resolver = context?.contentResolver

                imageUri?.apply {
                    resolver?.let {
                        val realPath = getRealPathFromUri(it, this)
                        realPath?.let { mPresenter.setMemberVariable(
                            EventField.EVENT_IMAGE_PATH, it
                        ) }
                    }
                }
                mPresenter.setImagePreview()
            }

            REQUEST_CAMERA -> {

                val imagePath = mPresenter.getImagePath(context!!)
                mPresenter.setMemberVariable(
                    EventField.EVENT_IMAGE_PATH,
                    imagePath.toString()
                )

                mPresenter.setImagePreview()

//                activity?.let {
//                    mFile?.apply {
//                        val uri = FileProvider.getUriForFile(it, AUTHORITY, this)
//                        it.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                        setPreviewWithFile()
//                    }
//                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.add_edit_menu_save -> {

                if (mTitle.text.isEmpty()) {
                    showEmptyTitleError()
                } else {

                    mPresenter.saveData()

                    activity?.setResult(RESULT_OK) // todo move th presneter setEventProperties methods

                    showSavedSnackbar()
                }

                return true
            }

            else -> {
                return false
            }
        }
    }

    private fun showEmptyTitleError() {
        view?.findViewById<TextInputLayout>(R.id.title_layout)?.apply {
            isErrorEnabled = true
            error = "Please enter a title"
        }
        vibrate(mTitle)
    }

    private fun showSavedSnackbar() {
        activity?.let {
            makeSnack(
                it.findViewById(R.id.add_edit_fg_container),
                resources.getText(R.string.save_snack_bar)
            )
            hideKeyboard(it as AppCompatActivity)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View) {

        when (p0.id) {

        // Pic picture btm sheet
            R.id.camera_fab -> makeBtmSheetPickPic()
            R.id.cancel_btn_pic -> mBtmSheet_PickPic.dismiss()
            R.id.camera_btn -> chooseCamera()
            R.id.gallery_btn -> chooseGallery()

        // date picker
            R.id.date_layout_edit -> mPresenter.makeDateDial()

        // Show BottomSheet to choose repeat mode
            R.id.repeat_layout_edit -> makeBtmSheetRepeat()

        // Repeat mode btm sheet
            R.id.never, R.id.weekly, R.id.monthly, R.id.annually -> {

                val repeatCode = mPresenter.getRepeatCodeFromViewId(p0.id)
                mPresenter.setMemberVariable(EventField.EVENT_REPEAT_MODE, repeatCode)

                setRepeatDrawable(repeatCode)

                mBtmSheet_RepeatMode.dismiss()
            }

        // toggle the star
            R.id.star_textview_addedit -> {
                mPresenter.toggleStar()
            }

        // Btm sheet for category
            R.id.category_textview_addedit -> {
                makeBtmSheetForCategory()
            }

        // Buttons in category bottomSheet
            R.id.cake, R.id.loved, R.id.face, R.id.social, R.id.work -> {

                // setEventProperties category field to mEvent
                val categoryCode: Int = mPresenter.getCategoryCodeFromViewId(p0.id)
                mPresenter.setMemberVariable(EventField.EVENT_CATEGORY, categoryCode)

                // view id to category code
                //val drawableResInt = viewIdToCategoryDrawableInt(p0.id)
                setCategoryDrawable(mCategoryTextView, categoryCode)
                //context?.let {  }

                mBtmSheet_Category.dismiss()
            }
        }
    }

    // For ui test in addEdit screen
    private fun viewIdToCategoryDrawableInt(viewId: Int): Int {
        return when (viewId) {
            R.id.cake -> R.drawable.ic_action_cake
            R.id.loved -> R.drawable.ic_action_loved
            R.id.face -> R.drawable.ic_action_face
            R.id.social -> R.drawable.ic_action_social
            R.id.work -> R.drawable.ic_action_work
            else -> R.drawable.ic_category_empty
        }
    }

    override fun setTitle(title: String) {
        mTitle.setText(title)
    }

    private fun setTitleChangeListener(titleEditText: TextInputEditText) {
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                checkTitleEmpty(s)

                checkMaxLength(titleEditText, s, 30)

                mPresenter.setMemberVariable(EventField.EVENT_TITLE, s.toString())
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun checkTitleEmpty(input: CharSequence) {

        view?.findViewById<TextInputLayout>(R.id.title_layout)?.error =
                if (input.isEmpty()) {
                    "Please enter a title"
                } else {
                    null
                }
    }

    private fun checkMaxLength(editText: TextInputEditText, input: CharSequence, maxLength: Int) {
        if (input.length >= maxLength) {
            editText.error = "Reached max length of $maxLength characters"
        }
    }

    private fun setDescriptionChangeListener(description: TextInputEditText) {
        description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkMaxLength(description, s, 100)
            }

            override fun afterTextChanged(s: Editable) {
                mPresenter.setMemberVariable(EventField.EVENT_DESCRIPTION, s.toString())
            }
        })
    }

    override fun setDescription(description: String?) {
        mDescription.setText(description)
        mDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mPresenter.setMemberVariable(EventField.EVENT_DESCRIPTION, s.toString())

                val descriptionMaxLength = 200

                if (s.toString().length >= descriptionMaxLength) {
                    mDescription.findViewById<TextInputLayout>(R.id.description_layout).error =
                            "Please make description less than $descriptionMaxLength characters"
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun setDate(eventDateStr: String) {
        mDate.text = eventDateStr
    }

    override fun setRepeatDrawable(repeatCode: Int) {

        mRepeatMode.text = repeatCodeToString(repeatCode)
    }

    override fun setStarColor(isStarred: Boolean) {
        context?.let {
            setStarColor(it, mStarTextView, isStarred)
        }
    }

    override fun setCategoryDrawable(categoryCode: Int) {

        context?.let { setCategoryDrawable(mCategoryTextView, categoryCode) }
    }

    override fun setPreview(imagePath: String) {
        activity?.let { setImageFileToImageView(it, mImagePreview, imagePath) }
    }

    private fun chooseGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Choose image from:"),
            REQUEST_GALLERY
        )

        mBtmSheet_PickPic.dismiss()
    }

    private fun chooseCamera() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        activity?.let {

            val fileDir = it.filesDir

            // if a file with this name already exists, override it.
            val mFile = File(fileDir, mPresenter.getTheEvent().photoFilename())

            mFile.apply {

                val uri = FileProvider.getUriForFile(it, AUTHORITY, this)

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                val cameras = it.packageManager
                    .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
                for (camera in cameras) {
                    it.grantUriPermission(
                        camera.activityInfo.packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(cameraIntent, REQUEST_CAMERA)
                mBtmSheet_PickPic.dismiss()
                // todo on result revoke permission
            }
        }

        mBtmSheet_PickPic.dismiss()
    }

    private fun makeBtmSheetPickPic() {

        context?.let {
            mBtmSheet_PickPic = BottomSheetDialog(it).also {
                it.apply {
                    setContentView(R.layout.dial_image_picker)

                    // this fg impl listener interface, thus can be passed in as a listener
                    findViewById<Button>(R.id.camera_btn)?.setOnClickListener(this@AddEditFragment)
                    findViewById<Button>(R.id.gallery_btn)?.setOnClickListener(this@AddEditFragment)
                    findViewById<Button>(R.id.cancel_btn_pic)?.setOnClickListener(this@AddEditFragment)

                    show()
                }
            }
        }
    }

    private fun makeBtmSheetRepeat() {

        context?.let {
            mBtmSheet_RepeatMode = BottomSheetDialog(it).also {
                it.apply {
                    setContentView(R.layout.dial_repeat_mode)

                    findViewById<TextView>(R.id.weekly)?.setOnClickListener(this@AddEditFragment)
                    findViewById<TextView>(R.id.monthly)?.setOnClickListener(this@AddEditFragment)
                    findViewById<TextView>(R.id.annually)?.setOnClickListener(this@AddEditFragment)
                    findViewById<TextView>(R.id.never)?.setOnClickListener(this@AddEditFragment)

                    show()
                }
            }
        }
    }

    private fun makeBtmSheetForCategory() {

        context?.let {

            mBtmSheet_Category = BottomSheetDialog(it).apply {

                setContentView(R.layout.dial_category)

                findViewById<ImageView>(R.id.cake)?.setOnClickListener(this@AddEditFragment)
                findViewById<ImageView>(R.id.loved)?.setOnClickListener(this@AddEditFragment)
                findViewById<ImageView>(R.id.face)?.setOnClickListener(this@AddEditFragment)
                findViewById<ImageView>(R.id.social)?.setOnClickListener(this@AddEditFragment)
                findViewById<ImageView>(R.id.work)?.setOnClickListener(this@AddEditFragment)

                show()
            }
        }
    }

    private fun hideKeyboard(activity: AppCompatActivity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun vibrate(view: View) {
        val vibrate = AnimationUtils.loadAnimation(context, R.anim.vibrate)
        view.startAnimation(vibrate)
    }

    companion object {

        private val REQUEST_DATE = 0
        private val REQUEST_CAMERA = 1
        private val REQUEST_GALLERY = 2
        private val DIALOG_DATE = "DialogDate"
        private val ARG_EVENT_ID = "arg_event_id"

        private const val AUTHORITY = "com.robyn.dayplus2.fileprovider"

        /**
         * To edit an existing mEvent
         */

        // no need to pass in the mPresenter, b/c this fg in added into the ac, and ac has the mPresenter...?
//        fun newInstance(mUuid: String, presenter: AddEditContract.Presenter): AddEditFragment {
//            val args = Bundle()
//            args.putString(ARG_EVENT_ID, mUuid)
//
//            val fragment = AddEditFragment()
//            fragment.arguments = args
//
//            this.mPresenter = presenter
//            return fragment
//        }

        /**
         * If mUuid == null, create new mEvent
         * else edit mEvent
         */
        fun newInstance(uuid: String?): AddEditFragment {
            val args = Bundle()
            args.putString(ARG_EVENT_ID, uuid)

            val fragment = AddEditFragment()
            fragment.arguments = args

            return fragment
        }

        /**
         * To create a new mEvent
         */
        //fun newInstance(): AddEditFragment = AddEditFragment()
    }
}