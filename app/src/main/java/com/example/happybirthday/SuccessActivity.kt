package com.example.happybirthday

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.happybirthday.databinding.ActivitySuccessBinding
import com.google.gson.Gson

class SuccessActivity : AppCompatActivity() {

    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySuccessBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        val apiResponseBody = intent.getStringExtra("apiResponseBody")
        val jsonResponse = gson.fromJson(apiResponseBody, FaceVerificationResponse::class.java)
        Log.d("JSON Response", jsonResponse.toString())
        binding.apiResponse.movementMethod = ScrollingMovementMethod()
        binding.apiResponse.text = apiResponseBody
        binding.uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }
}