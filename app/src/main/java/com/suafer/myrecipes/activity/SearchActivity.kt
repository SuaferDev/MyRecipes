package com.suafer.myrecipes.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suafer.myrecipes.R
import com.suafer.myrecipes.adapter.CustomRecipeAdapter
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.app.Viewer
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.dialog.PreviewDialog
import com.suafer.myrecipes.viewmodel.SearchViewModel
import com.suafer.myrecipes.viewmodel.SearchViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var listRecipes : RecyclerView

    private lateinit var textNew : TextView; private lateinit var textOld : TextView
    private lateinit var textAZ : TextView; private lateinit var textZA : TextView
    private lateinit var linearFilter : HorizontalScrollView
    private lateinit var linearNoElement : LinearLayout

    private lateinit var dataBase : MyRecipesDataBase

    private var recipes : List<Recipe> = listOf()
    private var sortRecipes : List<Recipe> = mutableListOf()

    private lateinit var viewModel : SearchViewModel

    private var navigationStatus : Boolean = true
    private lateinit var adapter: CustomRecipeAdapter

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
        viewModel = ViewModelProvider(this, SearchViewModelFactory(this))[SearchViewModel::class.java]

        listRecipes = findViewById(R.id.list_recipes)
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)
        val linearAdd = findViewById<LinearLayout>(R.id.linear_add)

        linearFilter = findViewById(R.id.linear_filter)

        textNew = findViewById(R.id.text_new)
        textOld = findViewById(R.id.text_old)
        textAZ = findViewById(R.id.text_az)
        textZA = findViewById(R.id.text_za)

        textNew.setOnClickListener {
            sortRecipes = Tool.sortOld(sortRecipes); updateSortList(); setFilter(textNew) }

        textOld.setOnClickListener {
            sortRecipes = Tool.sortNew(sortRecipes); updateSortList(); setFilter(textOld) }

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
                Viewer.rotate(imageArrow, 0f, 180f)
                false
            }else{
                setVisibility(View.GONE)
                Viewer.rotate(imageArrow, 180f, 0f)
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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                createWarning(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(listRecipes)


        linearAdd.setOnClickListener {
            val i = Intent(this, CreateActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    fun createWarning(position : Int){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.create_warning_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.window!!.statusBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.window!!.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        dialog.setCancelable(true)

        dialog.findViewById<TextView>(R.id.text_title).text = getString(R.string.delete_dialog_title)
        dialog.findViewById<TextView>(R.id.text_message).text = getString(R.string.delete_dialog_message)
        dialog.findViewById<TextView>(R.id.text_delete).text = getString(R.string.delete_dialog_delete)

        dialog.findViewById<TextView>(R.id.text_delete).setOnClickListener {
            deleteElement(position, dialog)
            updateSortList()
        }
        dialog.findViewById<TextView>(R.id.text_cansel).setOnClickListener {
            updateSortList()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteElement(position : Int, dialog: Dialog){
        lifecycleScope.launch (Dispatchers.IO){
            Tool.deleteImage(("recipe_" + adapter.getID(position)),this@SearchActivity)
            val steps = dataBase.dao().getAllSteps(adapter.getID(position))
            for(step in steps){
                Tool.deleteImage(("step_" + step.id), this@SearchActivity)
                dataBase.dao().deleteStep(step.id!!)
            }
            dataBase.dao().deleteRecipe(adapter.getID(position))
            withContext(Dispatchers.Main){
                dialog.dismiss()
            }
        }
    }

    private fun setFilter(textView : TextView){
        Viewer.clearAllFilter(this, textNew, textOld, textAZ, textZA)
        textView.setBackgroundResource(R.drawable.background_main_dark)
        textView.setTextColor(getColor(R.color.white))
    }

    private fun getRecipes(){
        if(UserData.instance.id != null){
            viewModel.getRecipe(UserData.instance.id!!)
            viewModel.resultLive.observe(this) { recipesList ->
                if(recipesList != null){
                    recipes = recipesList
                    updateList()
                }
            }
        }
    }

    private fun updateList(){
        adapter = CustomRecipeAdapter(this, recipes)
        listRecipes.adapter = adapter
        listRecipes.layoutManager = LinearLayoutManager(this)
        /*val adapter = CustomRecipeAdapter(this@SearchActivity, recipes)
        listRecipes.adapter = adapter*/
        setNoElement(recipes.size)
        sortRecipes = recipes
    }

    private fun updateSortList(){
        adapter = CustomRecipeAdapter(this, sortRecipes)
        listRecipes.adapter = adapter
        listRecipes.layoutManager = LinearLayoutManager(this)
        /*
        val adapter = CustomRecipeAdapter(this@SearchActivity, sortRecipes)
        listRecipes.adapter = adapter*/
    }

    private fun setNoElement(size : Int){
        linearNoElement.visibility = if(size == 0){ View.VISIBLE
        }else{ View.GONE }
    }

    private fun setVisibility(view: Int) { linearFilter.visibility = view }
}