package com.example.happybirthday

import android.content.Intent
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.happybirthday.databinding.ActivityCameraBinding
import com.example.happybirthday.databinding.ActivityFirebaseLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class FirebaseLoginActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityFirebaseLoginBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFirebaseLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this@FirebaseLoginActivity, CameraActivity::class.java))
        }
        
        viewBinding.signInButton.setOnClickListener {
            createSignInIntent()
        }
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@FirebaseLoginActivity, CameraActivity::class.java)
                startActivity(intent)
            }

            Toast.makeText(this@FirebaseLoginActivity, "Login success" + user.toString(), Toast.LENGTH_SHORT).show()
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this@FirebaseLoginActivity, "Login fail", Toast.LENGTH_SHORT).show()
        }
    }
}