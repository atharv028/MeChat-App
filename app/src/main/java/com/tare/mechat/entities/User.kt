package com.tare.mechat.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class User(
    val uid : String,
    val username : String,
    val imgUrl : String,
): Parcelable{
    constructor() : this ("", "", "")
}