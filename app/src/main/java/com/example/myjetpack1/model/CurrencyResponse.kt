package com.example.myjetpack1.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

data class CurrencyResponse(
    @SerialName("data") var data: ArrayList<Data> = arrayListOf(),
    val hasMore: Boolean? = null,
)

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "currency")
data class Data(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("currency_name")
    val currencyName: String?,
    @SerializedName("currency_symbol")
    val currencySymbol: String?,
    @SerializedName("country_code")
    var countryCode: String?,
    @SerializedName("currency_code")
    var currencyCode: String?
) : Parcelable

