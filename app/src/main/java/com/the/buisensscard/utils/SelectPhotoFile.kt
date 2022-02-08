package com.the.buisensscard.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.the.buisensscard.BuildConfig

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SelectPhotoFile(activity: Activity) {

    private var mPhotoFile: File? = null
    private var mActivity = activity

    companion object {
        private var INSTANCE: SelectPhotoFile? = null
        fun getInstance(activity: Activity): SelectPhotoFile {
            return INSTANCE ?: synchronized(this) {
                val instance = SelectPhotoFile(activity)
                instance
            }
        }
    }

    fun selectImageType() {
        selectImageType(mActivity)
    }

    fun getImageFile(): File {
        return mPhotoFile!!
    }

    private fun selectImageType(context: Context) {
        val items = arrayOf<CharSequence>(
            "Take Photo",
            "Cancel"
        )
        val builder = AlertDialog.Builder(context)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                requestStoragePermission(true)

            } else if (items[item] == "Choose from Library") {
                requestStoragePermission(false)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun requestStoragePermission(isCamera: Boolean) {
        Dexter.withActivity(mActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (isCamera) {
                            Log.e("TAG", "camera ")
                            dispatchTakePictureIntent()
                        } else {
                            // dispatchGalleryIntent()
                        }
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .withErrorListener { error ->
                Toast.makeText(mActivity, "Error occurred! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(mActivity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Toast.makeText(mActivity, ex.localizedMessage, Toast.LENGTH_SHORT).show()
                Log.d("file not", ex.localizedMessage)
                // Error occurred while creating the File
            }
            if (photoFile != null) {

                val photoURI = FileProvider.getUriForFile(
                    mActivity, BuildConfig .APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                mActivity.startActivityForResult(
                    takePictureIntent,
                    ConstantHelper.REQUEST_TAKE_PHOTO
                )
            }
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            File(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Businesscard")
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("Businesscard", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("our file", image.toString())
        return image
    }


    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = mActivity.contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


}