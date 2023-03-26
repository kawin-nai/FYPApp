package com.example.happybirthday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.happybirthday.databinding.ActivityModeSelectorBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class ModeSelectorActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityModeSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityModeSelectorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this@ModeSelectorActivity, FirebaseLoginActivity::class.java)
            startActivity(intent)
        }

        viewBinding.launchVerifyButton.setOnClickListener {
            val intent = Intent(this@ModeSelectorActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        viewBinding.launchUploadButton.setOnClickListener {
            val intent = Intent(this@ModeSelectorActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        viewBinding.logoutText.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this@ModeSelectorActivity, FirebaseLoginActivity::class.java))
                    }
                    else {
                        Toast.makeText(this@ModeSelectorActivity, "Sign out failed", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }
}