package com.the.businesscard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.the.businesscard.databinding.ActivityMainBinding

import com.the.businesscard.utils.ConstantHelper
import com.the.businesscard.utils.SelectPhotoFile

 class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var selectPhotoFile: SelectPhotoFile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        selectPhotoFile=SelectPhotoFile.getInstance(this@MainActivity,this)
        binding.btClickPhoto.setOnClickListener{
            selectPhotoFile.selectImageType()

        }

    }






}