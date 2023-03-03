package com.example.happybirthday

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.happybirthday.databinding.ActivityCameraBinding
import com.example.happybirthday.databinding.ActivityUploadBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService


// TODO : Add actual API link
// TODO : Verify name before submission
// TODO : Add loading screen (turn preview on/off)
// TODO : Add face detection before upload
// TODO : Add multiple stages of face upload
class UploadActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityUploadBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val db = Firebase.firestore

    private val client = OkHttpClient()
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, UploadActivity.REQUIRED_PERMISSIONS, UploadActivity.REQUEST_CODE_PERMISSIONS
            )
        }
        viewBinding.switchCamera.setOnClickListener {
//            viewBinding.loadingPanel.visibility = View.VISIBLE
//            turnOnPreview()
            if (!allPermissionsGranted())
                ActivityCompat.requestPermissions(
                    this,
                    UploadActivity.REQUIRED_PERMISSIONS,
                    UploadActivity.REQUEST_CODE_PERMISSIONS
                )
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        viewBinding.shutterButton.setOnClickListener { takePhoto() }
    }

    private fun startCamera () {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(720, 960)).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                // Get the CameraControl instance from camera
                val cameraControl = camera.cameraControl

                val viewFinder = viewBinding.viewFinder

                // Add touch to focus listener
                viewFinder.setOnTouchListener setOnTouchListener@{ view: View, motionEvent: MotionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                        MotionEvent.ACTION_UP -> {
                            val factory = viewFinder.meteringPointFactory
                            val point = factory.createPoint(motionEvent.x, motionEvent.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            cameraControl.startFocusAndMetering(action)
                            return@setOnTouchListener true
                        }
                        else -> return@setOnTouchListener false
                    }
                }

            } catch(exc: Exception) {
                Log.e(UploadActivity.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto () {
        // Turn off camera preview
//        turnOffPreview()
//        viewBinding.loadingPanel.visibility = View.VISIBLE
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
//            .format(System.currentTimeMillis())
        val rawName = viewBinding.personName.text.toString()
        Log.d("Name", "Name: $rawName")
        val name = rawName.replace(" ", "_")
        val currentTime = System.currentTimeMillis()
        val imageName = name + "_" + "$currentTime"
        Log.d("Name + Current Time", "Name: $imageName")

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FaceApp")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()



        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(UploadActivity.TAG, "Photo capture failed: ${exc.message}", exc)
//                    viewBinding.loadingPanel.visibility = View.GONE
//                    turnOnPreview()
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d(UploadActivity.TAG, msg)

                    // Test upload image
                    // Root file path of the saved image
                    val rootFilePath = "/storage/emulated/0/Pictures/FaceApp/"
                    val rawImagePath = "$imageName.jpg"
                    val justTakenFilePath = "$rootFilePath$rawImagePath"
                    val file = Uri.fromFile(File(justTakenFilePath))
                    val realRef = storageRef.child("application-data/upload_faces/${file.lastPathSegment}")
                    val uploadTask = realRef.putFile(file)

                    uploadTask.addOnFailureListener {
                        Log.d("Upload failed", it.toString())
                    }.addOnSuccessListener {
                        Log.d("Upload success", it.toString())
                        makeToast("Upload success")
                        realRef.downloadUrl.addOnSuccessListener { uri ->
                            Log.d("Upload URL", uri.toString())
                            uploadToFirestore(rawImagePath, uri.toString()) }
//                        run the callApi function and then turn on preview

//                        callApi("https://reqres.in/api/users/$rawImagePath")
                    }
//                    turnOnPreview()
                }
            }
        )
    }

    private fun uploadToFirestore(fileName: String, uploadedURL: String) {
        Log.d("Upload to Firestore", uploadedURL)
        val data = hashMapOf(
            "image_name" to fileName,
            "image_url" to uploadedURL,
        )
        db.collection("upload_faces")
            .document("upload")
            .set(data)
            .addOnSuccessListener {
                Log.d("Uploaded to Firestore $TAG", "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore upload error $TAG", "Error adding document", e)
            }
    }

    private fun callApi(apiUrl: String) {
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        val failMsg = "Error: API call failed"
        val unexpectedCode = "Error: Unexpected code"

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("API call invalid", e.toString())
                makeToast(failMsg)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("API call valid", response.toString())
                response.use {
                    if (!response.isSuccessful){
                        Log.d("API call failed", "$response")
                        makeToast(unexpectedCode)
                        throw IOException("Unexpected code $response")
                    }

                    for ((name, value) in response.headers) {
                        Log.d("API headers detail", "$name: $value")
                    }
                    val responseBody = response.body!!.string()
                    Log.d("API body", responseBody)
                    val intent = Intent(this@UploadActivity, SuccessActivity::class.java)
                    intent.putExtra("apiResponseBody", responseBody)
                    startActivity(intent)
                }
            }
        })
    }

    private fun makeToast(toastMsg: String) {
        runOnUiThread {
            Toast.makeText(baseContext, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "UploadActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).toTypedArray()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult $requestCode $REQUEST_CODE_PERMISSIONS")
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
//                finish()
            }
        }
    }
}