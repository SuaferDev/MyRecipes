package com.suafer.myrecipes.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suafer.myrecipes.database.MyRecipesDataBase


class RecipeViewModelFactory(private val dataBase: MyRecipesDataBase, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(dataBase, context) as T
    }
}