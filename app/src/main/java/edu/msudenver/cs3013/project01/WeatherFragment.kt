package edu.msudenver.cs3013.project01

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import edu.msudenver.cs3013.project01.api.WeatherApiService
import edu.msudenver.cs3013.project01.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val WEATHER_API_KEY = "YOUR API KEY HERE"
private const val LOCATION = "Denver"

class WeatherFragment : Fragment() {

    // TODO-complete: WEATHER API Retrofit and Moshi SETUP ----------------------------------
    private val retrofitWeather by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(
                MoshiConverterFactory.create()
            ).build()
    }
    private val theWeatherApiService by lazy {
        retrofitWeather.create(WeatherApiService::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO-complete: Call the getWeather() function
        getWeather()
    }

    // TODO-complete: WEATHER API Call -------------------------------------------------------
    private fun getWeather() {
        // TODO-complete: Call the weather api service to get the weather data
        val call = theWeatherApiService.getWeather(WEATHER_API_KEY, LOCATION)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("MainActivity", "Failed to get search results", t)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                // TODO-complete: Error handling for the response if it is successful or not
                if (response.isSuccessful) {

                    // Get the weather response body
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        /*
                        * TODO-complete: Obtain location and temperature details from the weather response and set the
                        *  text views to display the details.
                        *   - Images used for the weather icons are from https://www.weatherapi.com/ (Doesn't work with Glide)
                        * */
                        val weatherLoc = weatherResponse.location
                        val weatherCurr = weatherResponse.current

                        val wLoc: TextView? = view?.findViewById(R.id.main_weather_loc)
                        val wTemp: TextView? = view?.findViewById(R.id.main_weather_temp)

                        wLoc?.text = weatherLoc.name
                        wTemp?.text = "${weatherCurr.temp_f} F | ${weatherCurr.condition.text}"

                        // 7/18/23 - Hold off on this for now
//                        // based on temp_f value, change background color to warm or cold drawable gradient
//                        val wTempF = weatherCurr.temp_f
//                        setTempBg(wTempF)

                    } else {
                        Log.e("MainActivity", "Empty response body")
                    }
                } else {
                    Log.e(
                        "MainActivity",
                        "Failed to get search results\n${response.errorBody()?.string() ?: ""}"
                    )
                }
            }
        })
    }
}
