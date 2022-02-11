package com.the.businesscard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.the.businesscard.databinding.ActivityMainBinding

import com.the.businesscard.utils.SelectPhotoHelper

 class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var selectPhotoFile: SelectPhotoHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        selectPhotoFile=SelectPhotoHelper.getInstance(this@MainActivity,this)
        binding.btClickPhoto.setOnClickListener{
            selectPhotoFile.selectImageType()

        }

    }






}