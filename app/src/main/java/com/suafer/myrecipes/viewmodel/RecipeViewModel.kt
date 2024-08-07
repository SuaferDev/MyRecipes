package com.suafer.myrecipes.viewmodel
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val dataBase: MyRecipesDataBase, private val context: Context) : ViewModel() {

    val recipe = MutableLiveData<Recipe>()
    val steps = MutableLiveData<List<Step>>()
    val loading = MutableLiveData<Boolean>()
    val recipeImage = MutableLiveData<Bitmap?>()




    fun fetchRecipe(id: Int) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedRecipe = dataBase.dao().getRecipe(id)
            val fetchedSteps = dataBase.dao().getAllSteps(id)
            withContext(Dispatchers.Main) {
                recipe.value = fetchedRecipe!!
                steps.value = fetchedSteps
                loading.value = false
                fetchImage(id)
            }
        }
    }

    private fun fetchImage(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = Tool.getImage("recipe_$id", context)
            withContext(Dispatchers.Main) {
                recipeImage.value = bitmap
            }
        }
    }
}
