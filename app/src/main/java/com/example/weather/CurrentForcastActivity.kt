package com.example.weather

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CurrentForcastActivity : AppCompatActivity() {
    private lateinit var currWeatherIV : ImageView
    private lateinit var currWeatherTV : TextView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_forecast)
        currWeatherIV = findViewById(R.id.curr_weather_image)
        currWeatherTV = findViewById(R.id.curr_weather)
    }

    fun goBack(v: View) {
        finish()
    }
}