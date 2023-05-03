package com.example.weather

import android.text.Editable
import android.util.Log

class Weather {

    fun isValidLocation(s : String) : Boolean
    {
        // TODO: Ensure the location is actually valid. Probably use the Geocoder class?
        Log.w("Weather", s)
        return true
    }
}