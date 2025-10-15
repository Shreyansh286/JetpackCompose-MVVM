package com.example.myjetpack1.ui

import com.example.myjetpack1.model.CountryDataItem
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NavRoutes {
    const val COUNTRY_LIST = "countryList"
    const val SETTING_ROUTE = "setting/{countryDataJson}"
    const val UNIT_LIST = "unitList"
    const val SELECTED_UNIT_KEY = "selected_unit"
    // 2. Create a helper to convert the object to a URL-safe JSON string
    fun settingScreen(countryData: CountryDataItem): String {
        val json = Gson().toJson(countryData)
        val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
        return "setting/$encodedJson"
    }
}