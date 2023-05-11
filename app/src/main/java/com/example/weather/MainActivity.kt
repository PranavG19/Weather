package com.example.weather

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var button_1 : Button
    private lateinit var button_2 : Button
    private lateinit var button_3 : Button
    private lateinit var currentLocation : Button;
    private lateinit var buttonArray : Array<Button>
    private lateinit var targetLocation : EditText
    private lateinit var toast : Toast
    private lateinit var pref : SharedPreferences
    private lateinit var editor : Editor
    private lateinit var builder : AlertDialog.Builder
    private lateinit var adView : AdView
    private lateinit var adBuilder : AdRequest.Builder
    private lateinit var adRequest : AdRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Loading the ad
        adView = findViewById(R.id.adView)
        adBuilder = AdRequest.Builder()
        adRequest = adBuilder.build()
        adView.loadAd(adRequest)
        button_1 = findViewById(R.id.button_1)
        button_2 = findViewById(R.id.button_2)
        button_3 = findViewById(R.id.button_3)
        currentLocation = findViewById(R.id.currentLocation)
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation(this);
    }

    private fun getLastKnownLocation(context: Context) {
        Log.w("MainActivity", "Getting last known location!")
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.w("MainActivity", "Latitude: ${location.latitude} Longitude: ${location.longitude}")
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                    if (addresses.isNotEmpty()) {
                        val countryCode = addresses[0].countryCode
                        val city = addresses[0].locality
                        setCurrentLocation(city, countryCode);
                    }

                }
            }
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
                    overridePendingTransition(R.anim.fade_in_and_scale, 0)
                }
                else if(choice == 1)
                {
                    var myIntent : Intent = Intent(this, WeeklyForecastActivity::class.java)
                    weather.initWeeklyWeather(button.text.toString())
                    startActivity(myIntent)
                    overridePendingTransition(R.anim.slide_from_left, 0)
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

    private fun setCurrentLocation(city: String, countryCode: String) {
        var s = "$city, $countryCode"
        currentLocation.backgroundTintList = ContextCompat.getColorStateList(this, R.color.teal_700)
        currentLocation.text = "Current Location: $s"
        currentLocation.textSize = 20F
        currentLocation.setOnClickListener {
            Log.w("MainActivity", "Current Location Button Clicked!")
            builder = AlertDialog.Builder(this)
            builder.setTitle("Which kind of forecast would you like to view?")
            builder.setItems(arrayOf("Current", "Weekly", "Cancel"), DialogInterface.OnClickListener {dialog, choice ->
                Log.w("MA", "The user clicked on $choice")
                if(choice == 0)
                {
                    var myIntent : Intent = Intent(this, CurrentForcastActivity::class.java)
                    weather.initCurrentWeather(s)
                    startActivity(myIntent)
                    overridePendingTransition(R.anim.fade_in_and_scale, 0)
                }
                else if(choice == 1)
                {
                    var myIntent : Intent = Intent(this, WeeklyForecastActivity::class.java)
                    weather.initWeeklyWeather(s)
                    startActivity(myIntent)
                    overridePendingTransition(R.anim.slide_from_left, 0)
                }
            })
            builder.show()
        }
    }
}
