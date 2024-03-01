package fr.isen.lasri.androiderestaurant.NetWork

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Price(@SerializedName("price") val price : String):Serializable
