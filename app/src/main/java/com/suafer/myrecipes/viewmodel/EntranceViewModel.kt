package com.suafer.myrecipes.viewmodel

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntranceViewModel(
    private var dataBase : MyRecipesDataBase
) : ViewModel() {

    val resultLive = MutableLiveData<User?>()

    constructor(context: Context) : this(MyRecipesDataBase.get(context))

    fun insertUser(user: User, dialog: Dialog) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dao().insertUser(user)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
        }
    }

    fun getUser(login: String, password: String){
        var user : User?
        viewModelScope.launch(Dispatchers.IO) {
            user = dataBase.dao().getUser(login, password)
            withContext(Dispatchers.Main){
                resultLive.value = user
            }
        }
    }
}