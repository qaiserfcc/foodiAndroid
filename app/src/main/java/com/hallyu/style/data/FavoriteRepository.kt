package com.hallyu.style.data

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.hallyu.style.R
import com.hallyu.style.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import io.paperdb.Paper
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


@Singleton
class FavoriteRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager,
) : BaseRepository() {
    val favoriteAndProduct = MutableLiveData<MutableList<Product>>()
    private val listIdProductFavorite = MutableLiveData<List<String>>()

    fun fetchFavoriteAndProduct() {
        if (userManager.isLogged()) {
            val favList = Paper.book().read<MutableList<Product>>(FAVORITE_KEY)?: mutableListOf()
            favoriteAndProduct.postValue(favList)
            isSuccess.postValue(true)
            getListIdProductFavorite(favList)
        }
    }


    fun insertFavorite(fav: Product): MutableLiveData<Boolean> {
        val isFinish = MutableLiveData(false)

        if (userManager.isLogged()) {
            val favorites: MutableList<Product> = favoriteAndProduct.value ?: mutableListOf()
            favorites.add(fav)
            Paper.book().write(FAVORITE_KEY,favorites)
            getListIdProductFavorite(favorites)
            isFinish.postValue(true)
        }
        return isFinish
    }

    fun removeFavorite(favorite: Product) {
        if (userManager.isLogged()) {
            favoriteAndProduct.value?.let {
                it.remove(favorite)
                Paper.book().write(FAVORITE_KEY,it)
                favoriteAndProduct.postValue(it)
                getListIdProductFavorite(it)
            }
        }
    }

    private fun getListIdProductFavorite(favList: List<Product>) {
        if (userManager.isLogged()) {
            val list = mutableSetOf<String>()
            for (document in favList) {
                list.add(document.id)
            }
            listIdProductFavorite.postValue(list.toList())
        }
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        if (!userManager.isLogged()) {
            buttonView.visibility = View.INVISIBLE
        } else {
            buttonView.visibility = View.VISIBLE
            listIdProductFavorite.value?.let {
                if (it.contains(idProduct)) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_active
                    )
                    buttonView.tag=true
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_favorite_no_active
                    )
                    buttonView.tag=false
                }
            }
        }
    }
}