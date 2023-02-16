package com.example.happybirthday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var amount: EditText
    private lateinit var total: TextView
    private lateinit var seekBarTip: SeekBar
    private lateinit var seekBarPercent: TextView
    private lateinit var nextButton: Button
    private lateinit var cameraButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        name = findViewById(R.id.name)
        amount = findViewById(R.id.amount)
        total = findViewById(R.id.total)
        seekBarTip = findViewById(R.id.seekBarTip)
        seekBarPercent = findViewById(R.id.seekBarPercent)
        nextButton = findViewById(R.id.nextButton)
        cameraButton = findViewById(R.id.cameraButton)
        seekBarPercent.text = "0%"
        total.text = "$0.00"

        nextButton.setOnClickListener {
            Log.d(TAG, "Next Button clicked")
            // Add intent to go to next activity
            val intent = Intent(this, FetchActivity::class.java)
            startActivity(intent)
        }
        cameraButton.setOnClickListener {
            Log.d(TAG, "Camera Button clicked")
            // Add intent to go to camera activity
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarPercent.text = "$progress%"
                Log.i(TAG, "onProgressChanged: $progress")
                calculateTip()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTip()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun calculateTip() {
        val amountNum = amount.text.toString().toDoubleOrNull()
        if (amountNum != null) {
            val tip = amountNum * seekBarTip.progress / 100
            val totalNum = amountNum + tip
            total.text = "\$${String.format("%.2f", totalNum)}"
        }
        else{
            total.text = "\$0.00"
        }
    }
}