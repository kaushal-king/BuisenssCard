package com.the.buisensscard

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.the.buisensscard.databinding.ActivityCardResultBinding
import com.the.buisensscard.utils.ConstantHelper


class CardResult : AppCompatActivity() {

    private lateinit var binding: ActivityCardResultBinding
    var progressSeek: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE

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
            var bitmap = changeBitmapContrastBrightness(ConstantHelper.bitmap, 1.7f, 0f)
            Glide.with(this)
                .load(bitmap)

                .into(binding.ivImgResult)
            binding.ivImgResult.visibility = View.VISIBLE
            binding.pbImg.visibility = View.GONE
        }

        binding.btNormalMode.setOnClickListener {
            binding.ivImgResult.visibility = View.INVISIBLE
            binding.pbImg.visibility = View.VISIBLE
            Glide.with(this)
                .load(ConstantHelper.bitmap)

                .into(binding.ivImgResult)
            binding.ivImgResult.visibility = View.VISIBLE
            binding.pbImg.visibility = View.GONE
        }

        binding.btBlackWhite.setOnClickListener {
            converToGrayScale()
        }

        binding.sbContras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, p1: Int, p2: Boolean) {
                var progress: Float = (seek!!.progress / 10F)
                binding.tvContras.text = progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                var progress: Float = (seek!!.progress / 10F)
                progressSeek = progress
                changeContras(progress)
            }
        })

    }


    fun converToGrayScale() {
        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE
        var bitmap = changeBitmapContrastBrightness(ConstantHelper.bitmap, progressSeek, 0f)?.let {
            getGrayScaleBitmap(
                it
            )
        }
        Glide.with(this)
            .load(bitmap)
            .into(binding.ivImgResult)
        binding.ivImgResult.visibility = View.VISIBLE
        binding.pbImg.visibility = View.GONE
    }


    fun changeContras(contrast: Float) {
        binding.ivImgResult.visibility = View.INVISIBLE
        binding.pbImg.visibility = View.VISIBLE
        var bitmap = changeBitmapContrastBrightness(ConstantHelper.bitmap, contrast, 0f)

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


    fun changeBitmapContrastBrightness(bmp: Bitmap, contrast: Float, brightness: Float): Bitmap? {
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
}