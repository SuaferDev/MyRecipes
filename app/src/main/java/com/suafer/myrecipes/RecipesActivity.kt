package com.suafer.myrecipes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.suafer.myrecipes.adapter.CustomRecipeAdapter
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipesActivity : AppCompatActivity() {

    private lateinit var listRecipes : ListView

    private lateinit var textNew : TextView
    private lateinit var textOld : TextView
    private lateinit var textAZ : TextView
    private lateinit var textZA : TextView

    private lateinit var dataBase : MyRecipesDataBase
    private var recipes : List<Recipe> = listOf()
    private lateinit var linearNoElement : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        dataBase = MyRecipesDataBase.get(this)

        init()
        getRecipes()
    }

    private fun init(){
        listRecipes = findViewById(R.id.list_recipes)
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)

        textNew = findViewById(R.id.text_new)
        textOld = findViewById(R.id.text_old)
        textAZ = findViewById(R.id.text_az)
        textZA = findViewById(R.id.text_za)

        textNew.setOnClickListener { setFilter(textNew) }

        textOld.setOnClickListener { setFilter(textOld) }

        textAZ.setOnClickListener { setFilter(textAZ) }

        textZA.setOnClickListener { setFilter(textZA) }

        val imageAdd = findViewById<ImageView>(R.id.image_add)
        linearNoElement = findViewById(R.id.linear_no_element)

        listRecipes.setOnItemClickListener { _, _, position, _ ->
            createRecipePreviewDialog(recipes[position])
        }

        imageAdd.setOnClickListener {
            val res = Recipe(null, Tool.getTime(),"test", null, "te", 2, 123, "", UserData.instance.id!!)

            lifecycleScope.launch(Dispatchers.IO) {
                dataBase.dao().insertRecipe(res)
                updateList()
                withContext(Dispatchers.Main) {
                }
            }
            updateList()
        }
    }

    private fun createRecipePreviewDialog(recipe : Recipe){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.recipe_preview)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.setCancelable(true)

        //findViewById<ImageView>(R.id.image_food).setImageResource()
        dialog.findViewById<TextView>(R.id.text_name).text = recipe.name
        dialog.findViewById<TextView>(R.id.text_time).text = recipe.time.toString()
        dialog.findViewById<TextView>(R.id.text_kcal).text = recipe.calories.toString()
        dialog.findViewById<TextView>(R.id.text_ingredients).text = recipe.ingredients

        dialog.findViewById<TextView>(R.id.button_detail).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun clearAllFilter(){ clearFilter(textNew); clearFilter(textOld); clearFilter(textAZ); clearFilter(textZA) }

    private fun clearFilter(textView : TextView){
        textView.setBackgroundResource(R.drawable.corner_main_dark)
        textView.setTextColor(getColor(R.color.main_dark_extra))
    }

    private fun setFilter(textView : TextView){
        clearAllFilter()
        textView.setBackgroundResource(R.drawable.background_main_color)
        textView.setTextColor(getColor(R.color.white))
    }

    private fun getRecipes(){
        lifecycleScope.launch(Dispatchers.IO) {
            recipes = dataBase.dao().getAllRecipes(UserData.instance.id!!)
            withContext(Dispatchers.Main) {
                updateList()
            }
        }
    }

    private fun updateList(){
        val adapter = CustomRecipeAdapter(this@RecipesActivity, recipes)
        listRecipes.adapter = adapter
        setNoElement(recipes.size)
    }

    private fun setNoElement(size : Int){
        linearNoElement.visibility = if(size != 0){ View.VISIBLE
        }else{ View.GONE }
    }
}