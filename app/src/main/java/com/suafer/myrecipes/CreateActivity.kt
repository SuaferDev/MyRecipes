package com.suafer.myrecipes

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CreateActivity : AppCompatActivity() {

    /** Параметры рецепта **/
    private var name = ""; private var description = ""
    private var time = 0; private var calories = 0
    private var type = ""
    private val ingredients : MutableList<String> = mutableListOf()
    private val steps : MutableList<Step> = mutableListOf()

    /** **/
    private lateinit var editName : EditText
    private lateinit var editTime : EditText; private lateinit var editKcal : EditText
    private lateinit var editDescription : EditText
    private lateinit var edit_type: AutoCompleteTextView
    private lateinit var ingredientsGroup : ChipGroup
    private lateinit var editIngredients : EditText; private lateinit var editCount : EditText

    private lateinit var linearAddStep : LinearLayout

    private lateinit var dataBase : MyRecipesDataBase


    private lateinit var dialogLoading : Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        dataBase = MyRecipesDataBase.get(this)
        init()
    }

    private fun init(){
        editName = findViewById(R.id.edit_name)
        editTime = findViewById(R.id.edit_time); editKcal = findViewById(R.id.edit_kcal)
        editDescription = findViewById(R.id.edit_description)

        ingredientsGroup = findViewById(R.id.ingredientsGroup)
        editIngredients = findViewById(R.id.edit_ingredients); editCount = findViewById(R.id.edit_count)

        editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { name = editName.text.toString() }
            override fun afterTextChanged(editable: Editable) {}
        })

        editTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { time = editTime.text.toString().toInt() }
            override fun afterTextChanged(editable: Editable) {}
        })

        editKcal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { calories = editKcal.text.toString().toInt() }
            override fun afterTextChanged(editable: Editable) {}
        })

        editKcal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { calories = editKcal.text.toString().toInt() }
            override fun afterTextChanged(editable: Editable) {}
        })

        edit_type = findViewById(R.id.edit_type)
        val countries: Array<out String> = resources.getStringArray(R.array.food_type)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, countries).also { adapter ->
            edit_type.setAdapter(adapter)
        }

        findViewById<ImageView>(R.id.image_food).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.linear_add).setOnClickListener{
            createLoadingDialog()
            save()
        }

        findViewById<ImageView>(R.id.image_add).setOnClickListener {
            val ingredient = editIngredients.text.toString()
            val count = editCount.text.toString()
            if(ingredient.isEmpty() || count.isEmpty()){

            }else{
                ingredients.add("$ingredient ⨯$count")
                editIngredients.setText(""); editCount.setText("")
                updateIngredients()
            }
        }
    }

    private fun save(){
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val data : String = LocalDateTime.now().format(formatter)

        val recipe = Recipe(null, data, name, null, description, type, time, calories, stringList(), UserData.instance.id!!)

        lifecycleScope.launch(Dispatchers.IO) {
            dataBase.dao().insertRecipe(recipe)
            withContext(Dispatchers.Main) {
                dialogLoading.dismiss()
            }
        }
    }

    private fun createLoadingDialog() { /** Создание диалога загрузки **/
        dialogLoading = Dialog(this)
        dialogLoading.setContentView(R.layout.loading_dialog)
        dialogLoading.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogLoading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoading.window!!.attributes.windowAnimations = R.style.DialogLoadingAnim
        dialogLoading.setCancelable(false)

        val imageLoading = dialogLoading.findViewById<ImageView>(R.id.imageLoading)

        val rotateAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = Animation.INFINITE

        imageLoading.startAnimation(rotateAnimation)

        dialogLoading.show()
    }

    private fun updateIngredients(){
        ingredientsGroup.removeAllViews()
        for(ingredient in ingredients){
            val chip = Chip(this).apply {
                text = ingredient
                setTextColor(resources.getColor(R.color.black, theme))
                textSize = 20f
                typeface = ResourcesCompat.getFont(context, R.font.jost)
                setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0, com.google.android.material.R.style.Widget_Material3_ChipGroup))
                setOnCloseIconClickListener { ingredientsGroup.removeView(this); ingredients.remove(ingredient); updateIngredients() }
            }
            ingredientsGroup.addView(chip)
        }
    }


    private fun stringList() : String{
        var str = ""
        for(i in ingredients){ str += "$i " }
        return str
    }
}