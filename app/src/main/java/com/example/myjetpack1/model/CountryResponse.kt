package com.example.myjetpack1.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

data class CountryResponse(
	@SerialName("data") var data    : ArrayList<CountryDataItem> = arrayListOf(),
	val hasMore: Boolean? = null,
)
@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "countries")
data class CountryDataItem(
	@PrimaryKey
	@SerializedName("id") // Good practice to be explicit
	val id: Int,

	@SerializedName("short_name") // This tells Gson to map "short_name" from JSON to this property
	val shortName: String?,

	@SerializedName("country_flag")
	val countryFlag: String?,

	@SerializedName("iso_2") // Even if names match, being explicit can prevent issues
	val iso2: String?,

	@SerializedName("currency_code")
	val currencyCode: String?,
) : Parcelable

