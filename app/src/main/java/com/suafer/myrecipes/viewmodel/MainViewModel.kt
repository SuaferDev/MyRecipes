package com.suafer.myrecipes.viewmodel

import android.app.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(

) : ViewModel() {

    val resultLive = MutableLiveData<User?>()

    fun insertUser(user: User, dialog: Dialog, dataBase: MyRecipesDataBase) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dao().insertUser(user)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
        }
    }

    fun getUser(login: String, password: String, dataBase: MyRecipesDataBase){
        var user : User?
        viewModelScope.launch(Dispatchers.IO) {
            user = dataBase.dao().getUser(login, password)
            withContext(Dispatchers.Main){
                resultLive.value = user
            }
        }
    }
}