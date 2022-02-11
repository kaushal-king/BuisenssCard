package com.the.businesscard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.the.businesscard.databinding.ActivityCardResultBinding
import com.the.businesscard.utils.ConstantHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class CardResultActivity : AppCompatActivity() {
     lateinit var mPhotoFile: Uri
    lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityCardResultBinding
    var progressSeek: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE
        bitmap = ConstantHelper.bitmap
        Glide.with(this)
            .load(ConstantHelper.bitmap)

            .into(binding.ivImgResult)

        binding.ivImgResult.visibility = View.VISIBLE
        binding.pbImg.visibility = View.GONE

        binding.btAutoContras.setOnClickListener {
            binding.ivImgResult.visibility = View.INVISIBLE
            binding.pbImg.visibility = View.VISIBLE
            binding.sbContras.progress = 17
            progressSeek = 1.7f
            bitmap = bitmapContrastBrightness(ConstantHelper.bitmap, 1.7f, 0f)!!
            Glide.with(this)
                .load(bitmap)

                .into(binding.ivImgResult)
            binding.ivImgResult.visibility = View.VISIBLE
            binding.pbImg.visibility = View.GONE
        }

        binding.btNormalMode.setOnClickListener {
            binding.ivImgResult.visibility = View.INVISIBLE
            binding.pbImg.visibility = View.VISIBLE
            bitmap = ConstantHelper.bitmap
            Glide.with(this)
                .load(ConstantHelper.bitmap)

                .into(binding.ivImgResult)
            binding.ivImgResult.visibility = View.VISIBLE
            binding.pbImg.visibility = View.GONE
        }

        binding.btBlackWhite.setOnClickListener {
            convertToGrayScale()
        }

        binding.sbContras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, p1: Int, p2: Boolean) {
                val progress: Float = (seek!!.progress / 10F)
                binding.tvContras.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                val progress: Float = (seek!!.progress / 10F)
                progressSeek = progress
                changeContras(progress)
            }
        })



        binding.btSaveImg.setOnClickListener {
            saveImage(bitmap, "BusinessCard" + SimpleDateFormat("dd_MM_yyyy_HH_ss").format(Date()))
        }

    }


    fun convertToGrayScale() {
        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE
        bitmap = bitmapContrastBrightness(ConstantHelper.bitmap, progressSeek, 0f)?.let {
            getGrayScaleBitmap(
                it
            )
        }!!
        Glide.with(this)
            .load(bitmap)
            .into(binding.ivImgResult)
        binding.ivImgResult.visibility = View.VISIBLE
        binding.pbImg.visibility = View.GONE
    }


    fun changeContras(contrast: Float) {
        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE
        bitmap = bitmapContrastBrightness(ConstantHelper.bitmap, contrast, 0f)!!

        Glide.with(this)
            .load(bitmap)

            .into(binding.ivImgResult)
        binding.ivImgResult.visibility = View.VISIBLE
        binding.pbImg.visibility = View.GONE
    }


    fun getGrayScaleBitmap(original: Bitmap): Bitmap {
        val bmp = original.copy(Bitmap.Config.ARGB_8888, true)
        val bmpGrayscale = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(bmp, 0F, 0F, paint)
        return bmpGrayscale
    }


    fun bitmapContrastBrightness(bmp: Bitmap, contrast: Float, brightness: Float): Bitmap? {
        val cm = ColorMatrix(
            floatArrayOf(
                contrast,
                0f, 0f, 0f, brightness, 0f,
                contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bmp, 0.00f, 0.00f, paint)
        return ret
    }


    @Throws(IOException::class)
    private fun saveImage(bitmap: Bitmap, @NonNull name: String) {
        val outputStream: OutputStream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val imageUri: Uri? =
                resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            mPhotoFile= imageUri!!

            outputStream = resolver.openOutputStream(imageUri)!!
        } else {
           // val imagesDir =getExternalFilesDir("")
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString()
            Log.e("kaushal", "saveImage: $imagesDir", )

            val image = File(imagesDir, "$name.jpg")
            mPhotoFile=  FileProvider.getUriForFile(
                this,
                applicationContext.packageName.toString() + ".provider",
                image
            )
            outputStream = FileOutputStream(image)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        createNotification("$name.jpg save in Download successFully")
    }


    fun createNotification(description: String) {


        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        Log.e("path", "path: "+mPhotoFile, )
        intent.setDataAndType(mPhotoFile, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var notificationManager: NotificationManager? = null
        var channel: NotificationChannel? = null
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification =
            NotificationCompat.Builder(this, ConstantHelper.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setContentTitle("Photo Save Successfully")
                .setContentText(description + "")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setWhen(System.currentTimeMillis())
                .setVibrate(longArrayOf(0, 1000, 500, 1000))
                .setStyle( NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentIntent(pendingIntent)
                .setChannelId(ConstantHelper.NOTIFICATION_CHANNEL_ID)
                .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                ConstantHelper.NOTIFICATION_CHANNEL_ID, "Alaram Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            channel.enableVibration(true)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)


    }


}