package tarbi.metroexplorer.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PersistanceManager(context : Context) {
    private val sharedPreferences : SharedPreferences
    private val prefKey : String = "FAVORITES"

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun fetchFavorites() : List<Landmark> {
        // obtain JSON string of scores array
        var favoritesJson = sharedPreferences.getString(prefKey, null)

        // if null, this means no previous favorites, so create an empty array list
        if(favoritesJson == null) {
            return arrayListOf<Landmark>()
        }
        else {
            // existing favorites, so convert the favorites JSON string into Favorite objects, using Gson
            val favoritesType = object : TypeToken<MutableList<Landmark>>() {}.type
            val favorites: List<Landmark> = Gson().fromJson(favoritesJson, favoritesType)

            return favorites.sortedByDescending { it.name }
        }
    }

    fun saveFavorite(landmark: Landmark) {
        // fetch existing favorite landmarks
        val favorites = fetchFavorites().toMutableList()

        //add new score to it
        favorites.add(landmark)

        //persist to shared preferences
        val editor = sharedPreferences.edit()
        editor.putString(prefKey, Gson().toJson(favorites))

        editor.apply()
    }
}