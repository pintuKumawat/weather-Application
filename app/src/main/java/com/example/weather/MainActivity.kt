package com.example.weather

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//31f77f7e27ebda65475ad9f1aa187544--> reference of API
class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy { //Binding
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("jaipur")

        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })//by default function
    }

    private fun fetchWeatherData(cityName:String) {

        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterFace::class.java)
        val response=retrofit.getWeatherData(cityName,"31f77f7e27ebda65475ad9f1aa187544","metric")
        response.enqueue(object :Callback<weatherApp>{//by default function
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
            val responseBody=response.body()
                if (response.isSuccessful&&responseBody !=null){
                    val temperature=responseBody.main.temp.toString()
                    val humadity = responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"main"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_max

                    binding.temperater.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maximum.text="max Temp:$maxTemp °C"
                    binding.minimum.text="min Temp:$minTemp °C"
                    binding.humidity.text="$humadity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunRise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.seacondition.text="$seaLevel hPa"
                    binding.condition.text= condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"


                  //  Log.d("TAG","onResponse:$temperature")

                    changeImageAccordingToWeatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        }

    private fun changeImageAccordingToWeatherCondition(condition: String) {
        when(condition){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sun3)
                binding.lottieAnimationView.setAnimation(R.raw.sunny2)
            }
           "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.badal2)
            }
               "Rain","Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain1)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            "Smoke","Haze"->{
                binding.root.setBackgroundResource(R.drawable.d1)

            }
            else-> {
            binding.root.setBackgroundResource(R.drawable.sun3)
            binding.lottieAnimationView.setAnimation(R.raw.badal)
        }

        }

        binding.lottieAnimationView.playAnimation()



    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    } private fun time(timestamp: Long): String {
        val sdf=SimpleDateFormat("HH,mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
}