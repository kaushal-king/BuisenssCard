package com.the.businesscard

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.the.businesscard.databinding.ActivityMainBinding
import com.the.businesscard.utils.SelectPhotoHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
     private var selectPhotoFile: SelectPhotoHelper = SelectPhotoHelper.getInstance(this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.e("TAG", "onCreate: ")
        binding.btClickPhoto.setOnClickListener{
            selectPhotoFile.selectImageType()

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("imagePath",selectPhotoFile.getImageFile().toString())
        Log.e("TAG", "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectPhotoFile.setImageFile(savedInstanceState.get("imagePath").toString())
        Log.e("TAG", "onRestoreInstanceState: " + savedInstanceState.get("imagePath"))
    }

}