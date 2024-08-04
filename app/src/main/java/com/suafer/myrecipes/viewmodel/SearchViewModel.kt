package com.suafer.myrecipes.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private var dataBase : MyRecipesDataBase
) : ViewModel() {

    val resultLive = MutableLiveData<List<Recipe>>()

    constructor(context: Context) : this(MyRecipesDataBase.get(context))


    fun getRecipe(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = dataBase.dao().getAllRecipes(id)
            withContext(Dispatchers.Main) { resultLive.value = recipes }
        }
    }

    fun delete(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dao().deleteRecipe(id)
            val recipes = dataBase.dao().getAllRecipes(id)
            withContext(Dispatchers.Main) { resultLive.value = recipes }
        }
    }
}
