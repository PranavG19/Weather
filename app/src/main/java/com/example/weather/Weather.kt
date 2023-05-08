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
    private val PATTERN = Pattern.compile("^(?:[a-zA-Z]+\\s*)+,\\s*(?:[a-zA-Z]+\\s*)+\$")
    private lateinit var matcher : Matcher
    private var exceptionFlag : Boolean = false
    private var dailyInfo : WeatherInformation = WeatherInformation()
    private var weeklyInfo : Array<WeatherInformation> = Array(5) { WeatherInformation() }

    fun isValidLocation(s : String, isWeekly : Boolean) : Boolean
    {
        /*
            Ensure the location is actually valid.
            An example of an invalid call is:
            {"cod":"404","message":"city not found"}
        */
        Log.w("Weather", s)
        matcher = PATTERN.matcher(s)
        if(matcher.matches())
        {
            var tempString : String = String()
            tempString = if(!isWeekly)
                MainActivity.URL.replace("[TARGET]", s)
            else
                MainActivity.URL_FORCAST.replace("[TARGET]", s)
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
                true
            }
        }
        else
        {
            Log.w("Weather", "Invalid string: $s")
            return false
        }
    }

    fun initCurrentWeather(s : String)
    {
        if(isValidLocation(s, false))
        {
            var headJsonObject : JSONObject = JSONObject(jsonString)
            var tempJsonObject : JSONObject = headJsonObject.getJSONObject("coord")
            dailyInfo.longitude = tempJsonObject.getDouble("lon")
            dailyInfo.latitude = tempJsonObject.getDouble("lat")
            tempJsonObject = headJsonObject.getJSONArray("weather").getJSONObject(0)
            dailyInfo.mainWeather = tempJsonObject.getString("main")
            dailyInfo.description = tempJsonObject.getString("description")
            tempJsonObject = headJsonObject.getJSONObject("main")
            dailyInfo.temperatureKelvin = tempJsonObject.getDouble("temp")
            dailyInfo.feelsLikeKelvin = tempJsonObject.getDouble("feels_like")
            dailyInfo.tempMinKelvin = tempJsonObject.getDouble("temp_min")
            dailyInfo.tempMaxKelvin = tempJsonObject.getDouble("temp_max")
            dailyInfo.pressure = tempJsonObject.getDouble("pressure")
            dailyInfo.humidity = tempJsonObject.getDouble("humidity")
            dailyInfo.visibility = headJsonObject.getDouble("visibility")
            tempJsonObject = headJsonObject.getJSONObject("wind")
            dailyInfo.windSpeed = tempJsonObject.getDouble("speed")
            dailyInfo.windDegree = tempJsonObject.getDouble("deg")
            dailyInfo.timezone = headJsonObject.getDouble("timezone")
            tempJsonObject = headJsonObject.getJSONObject("sys")
            dailyInfo.country = tempJsonObject.getString("country")
            dailyInfo.sunriseTimestamp = tempJsonObject.getInt("sunrise")
            dailyInfo.sunsetTimestamp = tempJsonObject.getInt("sunset")
            dailyInfo.name = headJsonObject.getString("name")
        }
    }

    fun initWeeklyWeather(s : String)
    {
        if(isValidLocation(s, true))
        {
            for (i in weeklyInfo.indices)
            {
                var masterJsonObject: JSONObject = JSONObject(jsonString)
                var tempJsonArray : JSONArray = masterJsonObject.getJSONArray("list")
                var headJsonObject : JSONObject = tempJsonArray.getJSONObject(i shl 3)
                var tempJsonObject = headJsonObject.getJSONArray("weather").getJSONObject(0)
                weeklyInfo[i].mainWeather = tempJsonObject.getString("main")
                weeklyInfo[i].description = tempJsonObject.getString("description")
                tempJsonObject = headJsonObject.getJSONObject("main")
                weeklyInfo[i].temperatureKelvin = tempJsonObject.getDouble("temp")
                weeklyInfo[i].feelsLikeKelvin = tempJsonObject.getDouble("feels_like")
                weeklyInfo[i].tempMinKelvin = tempJsonObject.getDouble("temp_min")
                weeklyInfo[i].tempMaxKelvin = tempJsonObject.getDouble("temp_max")
                weeklyInfo[i].pressure = tempJsonObject.getDouble("pressure")
                weeklyInfo[i].humidity = tempJsonObject.getDouble("humidity")
                weeklyInfo[i].visibility = headJsonObject.getDouble("visibility")
                tempJsonObject = headJsonObject.getJSONObject("wind")
                weeklyInfo[i].windSpeed = tempJsonObject.getDouble("speed")
                weeklyInfo[i].windDegree = tempJsonObject.getDouble("deg")
                tempJsonObject = masterJsonObject.getJSONObject("city")
                weeklyInfo[i].timezone = tempJsonObject.getDouble("timezone")
                weeklyInfo[i].country = tempJsonObject.getString("country")
                weeklyInfo[i].sunriseTimestamp = tempJsonObject.getInt("sunrise")
                weeklyInfo[i].sunsetTimestamp = tempJsonObject.getInt("sunset")
                weeklyInfo[i].name = tempJsonObject.getString("name")
                tempJsonObject = tempJsonObject.getJSONObject("coord")
                weeklyInfo[i].longitude = tempJsonObject.getDouble("lon")
                weeklyInfo[i].latitude = tempJsonObject.getDouble("lat")
            }
        }
    }

    fun getDailyInfo() : WeatherInformation
    {
        return dailyInfo
    }

    fun getWeeklyInfo(i : Int) : WeatherInformation?
    {
        return if(i > 5)
            null
        else
            weeklyInfo[i]
    }

    inner class WeatherInformation
    {
        var longitude : Double = 0.0
        var latitude : Double = 0.0
        lateinit var mainWeather : String
        lateinit var description : String
        var temperatureKelvin : Double = 0.0
        var feelsLikeKelvin : Double = 0.0
        var tempMinKelvin : Double = 0.0
        var tempMaxKelvin : Double = 0.0
        var pressure : Double = 0.0
        var humidity : Double = 0.0
        var visibility : Double = 0.0
        var windSpeed : Double = 0.0
        var windDegree : Double = 0.0
        var timezone : Double = 0.0
        var sunriseTimestamp : Int = 0
        var sunsetTimestamp : Int = 0
        lateinit var country : String
        lateinit var name : String
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
