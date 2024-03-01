package fr.isen.lasri.androiderestaurant.NetWork

import com.google.gson.annotations.SerializedName

data class MenuData(@SerializedName("data")val data: List<Category>)
