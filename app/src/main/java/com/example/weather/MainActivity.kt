package com.example.weather

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var weather : Weather
    private lateinit var button_1 : Button
    private lateinit var button_2 : Button
    private lateinit var button_3 : Button
    private lateinit var targetLocation : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_1 = findViewById(R.id.button_1)
        button_2 = findViewById(R.id.button_2)
        button_3 = findViewById(R.id.button_3)
        targetLocation = findViewById(R.id.targetString)
        // TODO: Initialize buttons' text and colors to permanently-stored data.
        weather = Weather()
    }

    fun onLocationButtonClick(v: View)
    {
        Log.w("MA", "Location Button Clicked!")
        var button : Button = v as Button
        if(weather.isValidLocation(button.text.toString()))
        {
            // TODO: Transition to another screen!
        }
        else
        {
            // TODO: If the button has no valid location selected, don't do anything!
        }
    }

    fun onImageButtonClick(v: View)
    {
        Log.w("MA", "Search Bar Icon Clicked!")
        if(weather.isValidLocation(targetLocation.text.toString()))
        {
            // Shift the buttons' texts down by one and make the text of the topmost button to the location!
            button_3.text = button_2.text
            button_3.backgroundTintList = button_2.backgroundTintList
            button_2.text = button_1.text
            button_2.backgroundTintList = button_1.backgroundTintList
            button_1.text = targetLocation.text
            button_1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_700)
        }
        else
        {
            // TODO: Toast explaining that the location was invalid!
        }
    }

    fun getWeather() : Weather {
        return weather
    }

    companion object
    {
        val URL : String = "https://api.openweathermap.org/data/2.5/weather?q=[TARGET]&APPID=3d1c8d6c748afd572b690785579f6932"

        //Link gets 5 days forecast
        val URL_FORCAST = "https://api.openweathermap.org/data/2.5/forecast?q=[TARGET]&APPID=3d1c8d6c748afd572b690785579f6932"
    }
}