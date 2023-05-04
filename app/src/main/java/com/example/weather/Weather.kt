package com.example.weather

import android.content.Context
import android.text.Editable
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Weather {

    private var jsonString : String = ""
    private val PATTERN = Pattern.compile("\\w+, \\w+")
    private lateinit var matcher : Matcher
    private var exceptionFlag : Boolean = false

    private var longitude : Double = 0.0
    private var latitude : Double = 0.0
    private lateinit var mainWeather : String
    private lateinit var description : String
    private var temperatureKelvin : Double = 0.0
    private var feelsLikeKelvin : Double = 0.0
    private var tempMinKelvin : Double = 0.0
    private var tempMaxKelvin : Double = 0.0
    private var pressure : Double = 0.0
    private var humidity : Double = 0.0
    private var visibility : Double = 0.0
    private var windSpeed : Double = 0.0
    private var windDegree : Double = 0.0
    private var timezone : Double = 0.0
    private var sunriseTimestamp : Int = 0
    private var sunsetTimestamp : Int = 0
    private lateinit var country : String
    private lateinit var name : String

    fun isValidLocation(s : String) : Boolean
    {
        /*
            Ensure the location is actually valid.
            An example of an invalid call is:
            {"cod":"404","message":"city not found"}

            An example of a valid call is:
            {"coord":{"lon":-77.1528,"lat":39.084},
            "weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],
            "base":"stations",
            "main":{"temp":284.14,"feels_like":282.86,"temp_min":282.6,"temp_max":285.58,"pressure":1001,"humidity":60},
            "visibility":10000,
            "wind":{"speed":5.14,"deg":290,"gust":7.2},
            "clouds":{"all":100},
            "dt":1683146393,
            "sys":{"type":2,"id":2021196,"country":"US","sunrise":1683108496,"sunset":1683158557},
            "timezone":-14400,
            "id":4367175,
            "name":"Rockville",
            "cod":200}
        */
        Log.w("Weather", s)
        matcher = PATTERN.matcher(s)
        if(matcher.matches())
        {
            var tempString: String = MainActivity.URL.replace("[TARGET]", s)
            /*
            Replace the [TARGET] text tag with our actual target city and country.
            MUST be in the format:
                City, Country

            */
            var weatherThread: WeatherThread = WeatherThread(tempString)
            weatherThread.start()
            weatherThread.join()
            return if(exceptionFlag) {
                Log.w("Weather", "An exception was raised. Please try again...")
                false
            } else {
                Log.w("Weather", "Successful call! $jsonString")
                // Initialize this class's variables!
                var headJsonObject : JSONObject = JSONObject(jsonString)
                var tempJsonObject : JSONObject = headJsonObject.getJSONObject("coord")
                longitude = tempJsonObject.getDouble("lon")
                latitude = tempJsonObject.getDouble("lat")
                tempJsonObject = headJsonObject.getJSONArray("weather").getJSONObject(0)
                mainWeather = tempJsonObject.getString("main")
                description = tempJsonObject.getString("description")
                tempJsonObject = headJsonObject.getJSONObject("main")
                temperatureKelvin = tempJsonObject.getDouble("temp")
                feelsLikeKelvin = tempJsonObject.getDouble("feels_like")
                tempMinKelvin = tempJsonObject.getDouble("temp_min")
                tempMaxKelvin = tempJsonObject.getDouble("temp_max")
                pressure = tempJsonObject.getDouble("pressure")
                humidity = tempJsonObject.getDouble("humidity")
                visibility = headJsonObject.getDouble("visibility")
                tempJsonObject = headJsonObject.getJSONObject("wind")
                windSpeed = tempJsonObject.getDouble("speed")
                windDegree = tempJsonObject.getDouble("deg")
                timezone = headJsonObject.getDouble("timezone")
                tempJsonObject = headJsonObject.getJSONObject("sys")
                country = tempJsonObject.getString("country")
                sunriseTimestamp = tempJsonObject.getInt("sunrise")
                sunsetTimestamp = tempJsonObject.getInt("sunset")
                name = headJsonObject.getString("name")
                true
            }
        }
        else
        {
            Log.w("Weather", "Invalid string: $s")
            return false
        }
    }

    inner class WeatherThread : Thread
    {
        private lateinit var string : String

        constructor(string : String)
        {
            this.string = string
        }

        override fun run() : Unit
        {
            try
            {
                var url: URL = URL(string)
                var iStream: InputStream = url.openStream()
                var scan : Scanner = Scanner(iStream)
                jsonString = ""
                while(scan.hasNext())
                    jsonString += scan.nextLine()
                exceptionFlag = false
            }
            catch(e : Exception)
            {
                Log.w("WeatherThread", e.message!!)
                exceptionFlag = true
            }
        }
    }
}