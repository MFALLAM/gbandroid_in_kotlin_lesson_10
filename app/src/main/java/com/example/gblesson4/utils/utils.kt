package com.example.gblesson4.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.gblesson4.App
import com.example.gblesson4.model.City
import com.example.gblesson4.model.Weather
import com.example.gblesson4.model.dto.WeatherDTO
import com.example.gblesson4.model.getAddress
import com.example.gblesson4.model.room.WeatherEntity


fun convertDtoToWeather(weatherDTO: WeatherDTO): Weather {
    return Weather(getAddress(weatherDTO.info.lat, weatherDTO.info.lon),
        temperature = weatherDTO.fact.temp,
        feelsLike = weatherDTO.fact.feelsLike,
        icon = weatherDTO.fact.icon
    )
}

fun convertWeatherToEntity(weather: Weather): WeatherEntity {
    return weather.let {
        WeatherEntity(0, it.city.name, it.city.lat, it.city.lon,
            it.temperature, it.feelsLike, it.icon)
    }
}

fun convertEntityToWeather(entity: List<WeatherEntity>): List<Weather> {
    return entity.map {
        Weather(City(it.name, it.lat, it.lon), it.temperature, it.feelsLike, it.icon)
    }
}

fun checkPermission(activity: Activity, permission: String, title: String, message: String): Boolean {
    val permResult =
        ContextCompat.checkSelfPermission(App.appContext, permission)
    if (shouldShowRequestPermissionRationale(activity, permission)) {
        AlertDialog.Builder(App.appContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Предоставить доступ") { _, _ ->
                permissionRequest(activity, permission)
            }
            .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    } else if (permResult != PackageManager.PERMISSION_GRANTED) {
        permissionRequest(activity, permission)
    } else {
        return true
    }
    return false
}

private fun permissionRequest(activity: Activity, permission: String) {
    requestPermissions(activity, arrayOf(permission), REQUEST_CODE_READ_CONTACTS)
}

@SuppressLint("ServiceCast")
fun hideKeyboard(view: View) {
    val inputMethodManager = App.appContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}