package com.example.weather

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WeeklyForecastActivity : AppCompatActivity() {
    private lateinit var day1TV : TextView
    private lateinit var day2TV : TextView
    private lateinit var day3TV : TextView
    private lateinit var day4TV : TextView
    private lateinit var day5TV : TextView
    private lateinit var day1IV : ImageView
    private lateinit var day2IV : ImageView
    private lateinit var day3IV : ImageView
    private lateinit var day4IV : ImageView
    private lateinit var day5IV : ImageView



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_forecast)
        day1TV = findViewById(R.id.day1)
        day2TV = findViewById(R.id.day2)
        day3TV = findViewById(R.id.day3)
        day4TV = findViewById(R.id.day4)
        day5TV = findViewById(R.id.day5)
        day1IV = findViewById(R.id.image_day1)
        day2IV = findViewById(R.id.image_day2)
        day3IV = findViewById(R.id.image_day3)
        day4IV = findViewById(R.id.image_day4)
        day5IV = findViewById(R.id.image_day5)



    }

    fun goBack(v: View) {
        finish()
    }
}