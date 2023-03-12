package com.example.happybirthday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.happybirthday.adapters.RecycleAdapter
import com.example.happybirthday.databinding.ActivitySuccessBinding
import com.example.happybirthday.utilclasses.FaceDetail
import com.example.happybirthday.utilclasses.FaceVerificationResponse
import com.google.gson.Gson

//    TODO: Change distance to role or add role field
//    TODO: Change image depending on person
class SuccessActivity : AppCompatActivity() {

    private val gson = Gson()
    private var namesList = mutableListOf<String>()
    private var rolesList = mutableListOf<String>()
    private var distancesList = mutableListOf<String>()
    private var imagesList = mutableListOf<String>()
    private var fakeNamesList = mutableListOf<String>()
    private var fakeRolesList = mutableListOf<String>()
    private var fakeDistancesList = mutableListOf<String>()
    private var fakeImagesList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySuccessBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.backButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.rvRecyclerView.adapter = RecycleAdapter(this, namesList, rolesList, distancesList, imagesList)
        binding.rvRecyclerView.setHasFixedSize(true)
        binding.rvOtherView.adapter = RecycleAdapter(this, fakeNamesList, fakeRolesList, fakeDistancesList, fakeImagesList)
        binding.rvOtherView.setHasFixedSize(true)

        val apiResponseBody = intent.getStringExtra("apiResponseBody")
        val jsonResponse = gson.fromJson(apiResponseBody, FaceVerificationResponse::class.java)

        postToList(jsonResponse.content)

        Log.d("JSON Response", jsonResponse.toString())
//        binding.apiResponse.movementMethod = ScrollingMovementMethod()
//        binding.apiResponse.text = apiResponseBody
        binding.uploadButton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addToList(name: String, role: String, distance:String, image: String) {
        namesList.add(name)
        rolesList.add(role)
        distancesList.add(distance)
        imagesList.add(image)
    }

    private fun addToFakeList(name: String, role: String, distance:String, image: String) {
        fakeNamesList.add(name)
        fakeRolesList.add(role)
        fakeDistancesList.add(distance)
        fakeImagesList.add(image)
    }

    private fun postToList(faceList: ArrayList<FaceDetail>) {
        for (i in faceList) {
            if (i.distance < 0.5)
                addToList(i.person_name , i.role, i.distance.toString(), i.face_url)
            else
                addToFakeList(i.person_name, "Not a match", i.distance.toString(), i.face_url)
        }
    }
}