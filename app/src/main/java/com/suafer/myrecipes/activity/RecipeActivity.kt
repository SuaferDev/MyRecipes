package com.suafer.myrecipes.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.suafer.myrecipes.R
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.Viewer
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.Step
import com.suafer.myrecipes.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeActivity : AppCompatActivity() {

    private var id = 0

    private lateinit var recipe: Recipe
    private lateinit var steps : List<Step>

    private lateinit var dataBase : MyRecipesDataBase
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var imageFood : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        getData()
    }

    private fun init(){
        loadingDialog = LoadingDialog(this)
        dataBase = MyRecipesDataBase.get(this)
        id = intent.getIntExtra("ID_VALUE", -1)
        imageFood = findViewById(R.id.image_food)

        findViewById<ImageView>(R.id.image_close).setOnClickListener { finish() }

        findViewById<ImageView>(R.id.image_edit).setOnClickListener {
            val i = Intent(this, CreateActivity::class.java)
            i.putExtra("ID_VALUE", id)
            startActivity(i)
            finish()
        }
    }

    private fun setInfo(){
        findViewById<TextView>(R.id.text_name).text = recipe.name
        findViewById<TextView>(R.id.text_time).text = recipe.time.toString()
        findViewById<TextView>(R.id.text_kcal).text = recipe.calories.toString()
        findViewById<TextView>(R.id.text_type).text = recipe.type
        findViewById<TextView>(R.id.text_description).text = recipe.description
        Viewer.createIngredients(
            findViewById(R.id.ingredientsGroup),
            recipe.ingredients.split("/"),
            this
        )
        updateStep(
            findViewById(R.id.linear_steps),
            steps
        )
    }

    private fun getImage(){
        val bitmap = Tool.getImage("recipe_$id",this)
        if(bitmap != null){ Viewer.setImage(imageFood, bitmap)
        }
    }

    private fun getData(){
        loadingDialog.show()
        lifecycleScope.launch(Dispatchers.IO) {
            recipe = dataBase.dao().getRecipe(id)!!
            steps = dataBase.dao().getAllSteps(recipe.id!!)
            withContext(Dispatchers.Main){
                setInfo()
                getImage()
                loadingDialog.close()
            }
        }
    }

    private fun updateStep(linearSteps : LinearLayout, steps : List<Step>){
        linearSteps.removeAllViews()
        for (i in steps.indices) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.step_element, linearSteps, false)
            val textCount = itemView.findViewById<TextView>(R.id.text_count)
            val textTime = itemView.findViewById<TextView>(R.id.text_time)
            val textDescription = itemView.findViewById<TextView>(R.id.text_description)
            val image_food = itemView.findViewById<ImageView>(R.id.image_food)
            var str = (i + 1).toString()
            textCount.text = str
            str = "${steps[i].time} m"
            textTime.text = str
            textDescription.text = steps[i].description
            Viewer.setImage(image_food, Tool.getImage("step_${steps[i].id}", this))
            linearSteps.addView(itemView)
        }
    }
}