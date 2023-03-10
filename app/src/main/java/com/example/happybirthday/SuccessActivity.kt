package com.example.happybirthday

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happybirthday.databinding.ActivitySuccessBinding
import com.google.gson.Gson

//    TODO: Change distance to role or add role field
//    TODO: Change image depending on person
class SuccessActivity : AppCompatActivity() {

    private val gson = Gson()
    private var namesList = mutableListOf<String>()
    private var rolesList = mutableListOf<String>()
    private var imagesList = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySuccessBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.rvRecyclerView.adapter = RecycleAdapter(this, namesList, rolesList, imagesList)
        binding.rvRecyclerView.setHasFixedSize(true)

        val apiResponseBody = intent.getStringExtra("apiResponseBody")
        val jsonResponse = gson.fromJson(apiResponseBody, FaceVerificationResponse::class.java)

        postToList(jsonResponse.content)

        Log.d("JSON Response", jsonResponse.toString())
        binding.apiResponse.movementMethod = ScrollingMovementMethod()
        binding.apiResponse.text = apiResponseBody
        binding.uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addToList(name: String, role: String, image: Int) {
        namesList.add(name)
        rolesList.add(role)
        imagesList.add(image)
    }

    private fun postToList(faceList: ArrayList<FaceDetail>) {
        for (i in faceList) {
            addToList(i.person_name, i.distance.toString(), R.mipmap.ic_new_launcher_round)
        }
    }
}