package com.example.happybirthday

class FaceVerificationResponse (
    val content: ArrayList<FaceDetail>,
    val message: String,
    val verified: String,
)

data class FaceDetail(
    val distance: Double,
    val person_name: String,
)