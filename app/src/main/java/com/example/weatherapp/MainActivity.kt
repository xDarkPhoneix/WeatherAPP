package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
          fetchweatherdata("bhagalpur")
        searchcity()
    }

    private fun searchcity() {
        val searchView=binding.searchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0!=null){
                    fetchweatherdata(p0)

                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
               return true
            }

        })

    }


    private fun fetchweatherdata(cityname:String) {
       val retrofit= Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterface::class.java)

        val response=retrofit.getData(cityname,"04cbe560ca30465e25e2941cdcaa92d1","metric")
        response.enqueue(object :Callback<weatherApp> {
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responsebody=response.body()

                if(response.isSuccessful && responsebody!=null){
                    val humidity=responsebody.main.humidity.toString()
                    val windspeed=responsebody.wind.speed.toString()
                    val min_temp=responsebody.main.temp_min.toString()
                    val max_temp=responsebody.main.temp_max.toString()
                    val pressure=responsebody.main.pressure.toString()
                    val sunrise=responsebody.sys.sunrise.toLong()
                    val sunset=responsebody.sys.sunset.toLong()
                    val weatherType=responsebody.weather.firstOrNull()?.main
                 //  val weatherid=responsebody.weather.firstOrNull()?.id.toString()
                  //  Log.d("TAG",weatherid)
                    val temperature=responsebody.main.temp.toString()
                    binding.txttemperature.text="$temperature °C"
                    binding.txtweathertype.text=weatherType
                    binding.txthumidity.text="$humidity %"
                    binding.txtwindspeed.text="$windspeed m/s"
                    binding.txtxmaxtemperature.text="MAX $max_temp °C"
                    binding.txtxmintemperature.text="MIN $min_temp °C"
                    binding.txtsunrise.text="${time(sunrise)} AM"
                    binding.txtsunset.text="${time(sunset)} PM"
                    binding.txtsealevel.text="$pressure hpa"
                    binding.txtdescription.text=weatherType
                    binding.txtdate.text=date()
                    binding.txtday.text=dayNme(System.currentTimeMillis())
                    binding.txtcity.text="$cityname"


                    if (weatherType != null) {
                        changeaccordingtoweatherconditions(weatherType)
                    }

                }

            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {

            }
        })




    }

    private fun changeaccordingtoweatherconditions(conditions:String) {
       when(conditions){
           "Clear","Sunny","Clear Sky" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieanim.setAnimation(R.raw.sun)


           }

           "Rain" ,"Drizzle","SHowers","Thunderstorm"->{
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieanim.setAnimation(R.raw.rain)


           }

           "Snow","Light Snow","Moderate Snow","Blizzard","Heavy Snow" ->{
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieanim.setAnimation(R.raw.snow)


           }

           "Clouds","Haze" ,"Mist","Smoke","Dust","Partly Clouds","Fog"->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieanim.setAnimation(R.raw.cloud)


           }
           else->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieanim.setAnimation(R.raw.sun)
           }
       }
        binding.lottieanim.playAnimation()
    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }

    private fun dayNme(timestamp: Long): String {
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf=SimpleDateFormat("hh:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}


