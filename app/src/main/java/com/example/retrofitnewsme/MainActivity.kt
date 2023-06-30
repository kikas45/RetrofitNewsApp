package com.example.retrofitnewsme


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitnewsme.adapters.RecyclerAdapter
import com.example.retrofitnewsme.api.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    val BASE_URL = "https://api.currentsapi.services"

    lateinit var countdownTimer: CountDownTimer
    private var seconds = 3L

    private var arrayList = mutableListOf<News>()

    lateinit var v_blackScreen: View;
    lateinit var progressBar: ProgressBar;
    lateinit var rv_recyclerView: RecyclerView;
    lateinit var tv_noInternetCountDown: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        v_blackScreen = findViewById(R.id.v_blackScreen)
        progressBar = findViewById(R.id.progressBar)
        rv_recyclerView = findViewById(R.id.rv_recyclerView)
        tv_noInternetCountDown = findViewById(R.id.tv_noInternetCountDown)

        makeAPIRequest()
    }

    //simple fade in animation for when the app is done loading
    private fun fadeIn() {
        v_blackScreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }

    //requests data from the api and forwards it to the recycler view

    @SuppressLint("SuspiciousIndentation")
    private fun makeAPIRequest() {
        progressBar.visibility = View.VISIBLE


        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIRequest::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.getNews()


                for (article in response.news) {
                    Log.d("MainActivity", "Result + $article")
                    arrayList.add(article)
                }

                //updates ui when data has been retrieved
                withContext(Dispatchers.Main) {
                    setUpRecyclerView()
                    fadeIn()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.d("MainActivity", e.toString())
                withContext(Dispatchers.Main) {
                    attemptRequestAgain(seconds)

                }
            }
        }
    }


    private fun attemptRequestAgain(seconds: Long) {
        countdownTimer = object : CountDownTimer(seconds * 1010, 1000) {
            override fun onFinish() {
                makeAPIRequest()
                countdownTimer.cancel()
                tv_noInternetCountDown.visibility = View.GONE
                this@MainActivity.seconds += 3
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tv_noInternetCountDown.visibility = View.VISIBLE
                tv_noInternetCountDown.text =
                    "Cannot retrieve data...\nTrying again in: ${millisUntilFinished / 1000}"
                Log.d(
                    "MainActivity",
                    "Could not retrieve data. Trying again in ${millisUntilFinished / 1000} seconds"
                )
            }
        }
        countdownTimer.start()
    }

    private fun setUpRecyclerView() {
        rv_recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        rv_recyclerView.adapter = RecyclerAdapter(arrayList)
    }


}