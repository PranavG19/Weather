package com.example.weather

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var button_1 : Button
    private lateinit var button_2 : Button
    private lateinit var button_3 : Button
    private lateinit var buttonArray : Array<Button>
    private lateinit var targetLocation : EditText
    private lateinit var toast : Toast
    private lateinit var pref : SharedPreferences
    private lateinit var editor : Editor
    private lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_1 = findViewById(R.id.button_1)
        button_2 = findViewById(R.id.button_2)
        button_3 = findViewById(R.id.button_3)
        targetLocation = findViewById(R.id.targetString)
        // Initialize buttons' text and colors to permanently-stored data.
        pref = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
        button_1.text = pref.getString("button_1", getString(R.string.default_button_text))
        button_2.text = pref.getString("button_2", getString(R.string.default_button_text))
        button_3.text = pref.getString("button_3", getString(R.string.default_button_text))
        buttonArray = arrayOf(button_1, button_2, button_3)
        for(b in buttonArray)
        {
            if(b.text.toString() != getString(R.string.default_button_text))
            {
                b.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_700)
            }
        }
        editor = pref.edit()
    }

    fun onLocationButtonClick(v: View)
    {
        Log.w("MA", "Location Button Clicked!")
        var button : Button = v as Button
        if(weather.isValidLocation(button.text.toString(), false))
        {
            builder = AlertDialog.Builder(this)
            builder.setTitle("Which kind of forecast would you like to view?")
            builder.setItems(arrayOf("Current", "Weekly", "Cancel"), DialogInterface.OnClickListener {dialog, choice ->
                Log.w("MA", "The user clicked on $choice")
                if(choice == 0)
                {
                    var myIntent : Intent = Intent(this, CurrentForcastActivity::class.java)
                    weather.initCurrentWeather(button.text.toString())
                    startActivity(myIntent)
                }
                else if(choice == 1)
                {
                    var myIntent : Intent = Intent(this, WeeklyForecastActivity::class.java)
                    weather.initWeeklyWeather(button.text.toString())
                    startActivity(myIntent)
                }
            })
            builder.show()
        }
        else
        {
            // If the button has no valid location selected, display a toast!
            displayFailureToast()
        }
    }

    fun onImageButtonClick(v: View)
    {
        Log.w("MA", "Search Bar Icon Clicked!")
        if(weather.isValidLocation(targetLocation.text.toString(), false))
        {
            // Shift the buttons' texts down by one and make the text of the topmost button to the location!
            button_3.text = button_2.text
            button_3.backgroundTintList = button_2.backgroundTintList
            button_2.text = button_1.text
            button_2.backgroundTintList = button_1.backgroundTintList
            button_1.text = targetLocation.text
            button_1.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_700)
            for(i in buttonArray.indices)
            {
                editor.putString(("button_" + (i+1).toString()), buttonArray[i].text.toString())
            }
            editor.commit()
        }
        else
        {
            // Toast explaining that the location was invalid!
            displayFailureToast()
        }
    }

    fun getWeather() : Weather {
        return weather
    }

    private fun displayFailureToast() : Unit
    {
        Log.w("MA", "Invalid location! Displaying toast...")
        toast = makeText( this, "Please input a valid city in the following format:\nCITY, COUNTRY", Toast.LENGTH_LONG)
        toast.show()
    }

    companion object
    {
        val URL : String = "https://api.openweathermap.org/data/2.5/weather?q=[TARGET]&APPID=3d1c8d6c748afd572b690785579f6932"

        //Link gets 5 days forecast
        val URL_FORCAST = "https://api.openweathermap.org/data/2.5/forecast?q=[TARGET]&APPID=3d1c8d6c748afd572b690785579f6932"

        val weather = Weather()
    }
}