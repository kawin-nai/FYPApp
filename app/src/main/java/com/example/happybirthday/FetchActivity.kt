package com.example.happybirthday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.happybirthday.utilclasses.CurrencyResponse
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val TAG = "FetchActivity"

class FetchActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var currencyOne: TextView
    private lateinit var currencyTwo: TextView
    private lateinit var currencyThree: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        currencyOne = findViewById(R.id.currencyOne)
        currencyTwo = findViewById(R.id.currencyTwo)
        currencyThree = findViewById(R.id.currencyThree)

        getCurrencyData().start()
    }

    private fun getCurrencyData(): Thread {
        return Thread {
            val url = URL("https://open.er-api.com/v6/latest/usd")
            val connection = url.openConnection() as HttpsURLConnection
            val testResponse = url.readText()
            if (connection.responseCode == 200){
                val inputData = connection.inputStream
                val inputStreamReader = InputStreamReader(inputData, "UTF-8")
                val response = Gson().fromJson(inputStreamReader, CurrencyResponse::class.java)
                Log.d(TAG, "getCurrencyData: ${response.rates}")
                println(response)
                updateUI(response)


            }
            else{
                Log.e(TAG, "Error")
            }
        }

    }

    private fun updateUI(response: CurrencyResponse) {
        runOnUiThread {
            kotlin.run {
                currencyOne.text = "THB: " + response.rates.THB.toString()
                currencyTwo.text = "GBP: " + response.rates.GBP.toString()
                currencyThree.text = "AUD: " + response.rates.AUD.toString()
            }
        }
    }
}