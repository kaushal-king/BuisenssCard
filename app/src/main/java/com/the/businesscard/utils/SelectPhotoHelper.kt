package com.the.businesscard.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.the.businesscard.BuildConfig
import com.the.businesscard.CardCropActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SelectPhotoHelper(activity: Activity, componentActivity: ComponentActivity) {

    private var mPhotoFile: File? = null
    private var mActivity = activity


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        componentActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                 Log.e("TAGee", mPhotoFile.toString())
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(mPhotoFile!!.path, options)
                val intent = Intent(mActivity, CardCropActivity::class.java)
                ConstantHelper.bitmap = bitmap
                mActivity.startActivity(intent)

            }


        }


    private var activityCameraResultLauncher = componentActivity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.containsValue(false)) {
            dispatchTakePictureIntent()
        }
//        permissions.entries.forEach {
//            if(!it.value)
//            {
//                Log.e("permission", "${it.key} permission not grant", )
//            }
//          //  Log.e("DEBUG", "${it.key} = ${it.value}")
//        }
    }


    companion object {
        private var INSTANCE: SelectPhotoHelper? = null
        fun getInstance(
            activity: Activity,
            componentActivity: ComponentActivity
        ): SelectPhotoHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = SelectPhotoHelper(activity, componentActivity)
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
    fun setImageFile(path:String) {
        mPhotoFile=File(path)
    }

    private fun selectImageType(context: Context) {
        val items = arrayOf<CharSequence>(
            "Take Photo",
            "Cancel"
        )
        val builder = AlertDialog.Builder(context)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            when {
                items[item] == "Take Photo" -> {
                    requestStoragePermission()
                }
//                items[item] == "Choose from Library" -> {
//                    requestStoragePermission(false)
//                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun requestStoragePermission() {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        when {

            hasPermissions(mActivity, *permission) -> {
                Log.e("permission", "camera has permission ")
                dispatchTakePictureIntent()
            }
            else -> {
                Toast.makeText(mActivity, " Allow the Storage Permission", Toast.LENGTH_LONG).show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            Log.e("permission", "hasPermissions: $permissions")
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
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
                    mActivity, BuildConfig.APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                Log.e("TAG", mPhotoFile.toString())
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
               // takePictureIntent.putExtra("kk", mPhotoFile.toString())
                activityResultLauncher.launch(takePictureIntent)


                //activityImage.launch(photoURI)
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            File(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Businesscard")
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("BusinessCard", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("our file", image.toString())
        return image
    }


//    fun getRealPathFromUri(contentUri: Uri?): String? {
//        var cursor: Cursor? = null
//        return try {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
//            cursor = mActivity.contentResolver.query(contentUri!!, proj, null, null, null)
//            assert(cursor != null)
//            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            cursor.getString(columnIndex)
//        } finally {
//            cursor?.close()
//        }
//    }


}