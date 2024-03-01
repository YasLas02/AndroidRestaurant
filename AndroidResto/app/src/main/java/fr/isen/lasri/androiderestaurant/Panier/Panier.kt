package fr.isen.lasri.androiderestaurant.Panier

import android.content.Context
import com.google.gson.GsonBuilder
import fr.isen.lasri.androiderestaurant.NetWork.Dish

class Panier {
    var items: MutableList<PanierElem> = mutableListOf()

    fun add(dish: Dish, count: Int, context: Context) {
        val existingItem = items.firstOrNull { it.dish == dish }
        existingItem?.let {
            it.count = it.count + count
        } ?: run {
            items.add(PanierElem(count, dish))
        }
        save(context)
    }

    fun delete(item: PanierElem, context: Context) {
        items.removeAll { item.dish.name == it.dish.name }
        save(context)
    }

    fun save(context: Context) {
        val json = GsonBuilder().create().toJson(this)

        val sharedPreferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(BASKET_PREFERENCES_KEY, json)
        editor.apply()
    }

    companion object {
        fun current(context: Context): Panier {
            val sharedPreferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val json = sharedPreferences.getString(BASKET_PREFERENCES_KEY, null)
            if(json != null) {
                return GsonBuilder().create().fromJson(json, Panier::class.java)
            }
            return Panier()
        }

        val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
        val BASKET_PREFERENCES_KEY = "BASKET_PREFERENCES_KEY"
    }
}