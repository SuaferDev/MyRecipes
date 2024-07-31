package com.suafer.myrecipes.activity

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.suafer.myrecipes.R
import com.suafer.myrecipes.adapter.CustomRecipeAdapter
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.app.Viewer
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.dialog.PreviewDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var listRecipes : ListView

    private lateinit var textNew : TextView; private lateinit var textOld : TextView
    private lateinit var textAZ : TextView; private lateinit var textZA : TextView

    private lateinit var linearFilter : HorizontalScrollView
    private lateinit var linearType : HorizontalScrollView

    private lateinit var i : Intent

    private lateinit var dataBase : MyRecipesDataBase
    private var recipes : List<Recipe> = listOf()
    private lateinit var linearNoElement : LinearLayout

    private var sortRecipes : List<Recipe> = mutableListOf()

    private var navigationStatus : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        init()
        getRecipes()

    }

    private fun init(){
        dataBase = MyRecipesDataBase.get(this)

        listRecipes = findViewById(R.id.list_recipes)
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)
        val linearAdd = findViewById<LinearLayout>(R.id.linear_add)

        linearFilter = findViewById(R.id.linear_filter)
        linearType = findViewById(R.id.linear_type)

        textNew = findViewById(R.id.text_new)
        textOld = findViewById(R.id.text_old)
        textAZ = findViewById(R.id.text_az)
        textZA = findViewById(R.id.text_za)

        textNew.setOnClickListener {
            sortRecipes = Tool.sortNew(sortRecipes); updateSortList(); setFilter(textNew) }

        textOld.setOnClickListener {
            sortRecipes = Tool.sortOld(sortRecipes); updateSortList(); setFilter(textOld) }

        textAZ.setOnClickListener {
            sortRecipes = Tool.sortAZ(sortRecipes); updateSortList(); setFilter(textAZ) }

        textZA.setOnClickListener {
            sortRecipes = Tool.sortZA(sortRecipes); updateSortList(); setFilter(textZA) }

        val linearTopMenu = findViewById<LinearLayout>(R.id.linear_top_menu)
        val imageArrow = findViewById<ImageView>(R.id.image_arrow)

        linearTopMenu.setOnClickListener{
            TransitionManager.beginDelayedTransition(linearTopMenu)
            navigationStatus = if(navigationStatus){
                setVisibility(View.VISIBLE)
                rotateImage(imageArrow, 0f, 180f)
                false
            }else{
                setVisibility(View.GONE)
                rotateImage(imageArrow, 180f, 0f)
                true
            }
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                sortRecipes = Tool.findByName(recipes, editTextSearch.text.toString())
                updateSortList()
            }
            override fun afterTextChanged(editable: Editable) {}
        })


        linearNoElement = findViewById(R.id.linear_no_element)

        listRecipes.setOnItemClickListener { _, _, position, _ -> PreviewDialog.show(this, recipes[position]) }

        linearAdd.setOnClickListener {
            val i = Intent(this, CreateActivity::class.java)
            startActivity(i)
        }
    }

    private fun setFilter(textView : TextView){
        Viewer.clearAllFilter(this, textNew, textOld, textAZ, textZA)
        textView.setBackgroundResource(R.drawable.background_main_dark)
        textView.setTextColor(getColor(R.color.white))
    }

    private fun getRecipes(){
        lifecycleScope.launch(Dispatchers.IO) {
            if(UserData.instance.id != null){
                recipes = dataBase.dao().getAllRecipes(UserData.instance.id!!)
                withContext(Dispatchers.Main) {
                    updateList()
                }
            }

        }
    }

    private fun updateList(){
        val adapter = CustomRecipeAdapter(this@SearchActivity, recipes)
        listRecipes.adapter = adapter
        setNoElement(recipes.size)
        sortRecipes = recipes
    }

    private fun updateSortList(){
        val adapter = CustomRecipeAdapter(this@SearchActivity, sortRecipes)
        listRecipes.adapter = adapter
    }

    private fun setNoElement(size : Int){
        linearNoElement.visibility = if(size == 0){ View.VISIBLE
        }else{ View.GONE }
    }

    private fun setVisibility(view: Int) {
        linearFilter.visibility = view
        linearType.visibility = view
        //linearProfile.visibility = view
        //linearTime.visibility = view
    }

    private fun rotateImage(imageView: ImageView, from : Float, to : Float){
        val rotationAnimation = ObjectAnimator.ofFloat(imageView, "rotation", from, to)
        rotationAnimation.duration = 500
        rotationAnimation.start()
    }
}