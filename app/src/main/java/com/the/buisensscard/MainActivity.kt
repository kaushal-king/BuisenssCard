package com.the.buisensscard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.the.buisensscard.databinding.ActivityMainBinding
import com.the.buisensscard.utils.ConstantHelper
import com.the.buisensscard.utils.SelectPhotoFile

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var selectPhotoFile: SelectPhotoFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        selectPhotoFile=SelectPhotoFile.getInstance(this@MainActivity)
        binding.btClickPhoto.setOnClickListener{
            selectPhotoFile.selectImageType()

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == ConstantHelper.REQUEST_TAKE_PHOTO) {
                val options = BitmapFactory.Options()

                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                var bitmap = BitmapFactory.decodeFile(selectPhotoFile.getImageFile().path,options)
                val intent = Intent(this, CardCrop::class.java)
                ConstantHelper.bitmap=bitmap
                startActivity(intent)
            }
        }

    }

}