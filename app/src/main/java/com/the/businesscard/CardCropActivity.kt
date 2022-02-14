package com.the.businesscard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.the.businesscard.databinding.ActivityCardCropBinding
import com.the.businesscard.utils.ConstantHelper
import me.pqpo.smartcropperlib.SmartCropper

class CardCropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardCropBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardCropBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        SmartCropper.buildImageDetector(this)

        binding.ivCrop.setImageToCrop(ConstantHelper.bitmap)
        binding.btCropPhoto.visibility = View.VISIBLE

        binding.btCropPhoto.setOnClickListener {
            val crop: Bitmap = binding.ivCrop.crop()
            Glide.with(this)
                .load(crop)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.imageloading)
                )
                .into(binding.ivCrop)
            ConstantHelper.bitmap = crop
            binding.btCropPhoto.visibility = View.GONE
            binding.btRotate.visibility = View.VISIBLE

        }

        binding.btRotate.setOnClickListener {
            val crop: Bitmap = rotateBitmap(ConstantHelper.bitmap, 90.0F)
            Glide.with(this)
                .load(crop)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.imageloading)
                )
                .into(binding.ivCrop)
            ConstantHelper.bitmap = crop

        }

        binding.ibCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btSaveCrop.setOnClickListener {
            val intent = Intent(this, CardResultActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}