package com.suafer.myrecipes.viewmodel

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(
    private var dataBase : MyRecipesDataBase
) : ViewModel() {

    val resultLive = MutableLiveData<User?>()

    constructor(context: Context) : this(MyRecipesDataBase.get(context))

    fun getSteps(id: Int, dialog: Dialog) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dao().getStep(id)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
        }
    }
}