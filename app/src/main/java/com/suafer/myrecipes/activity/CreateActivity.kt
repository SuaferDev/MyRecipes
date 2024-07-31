package com.suafer.myrecipes.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.suafer.myrecipes.R
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.app.Viewer.Companion.setDefaultEdit
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.Step
import com.suafer.myrecipes.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.suafer.myrecipes.app.Viewer.Companion.hintColor


class CreateActivity : AppCompatActivity() {

    /** Параметры рецепта **/
    private var name = ""; private var description = ""
    private var time = 0; private var calories = 0
    private var type = ""
    private val ingredients : MutableList<String> = mutableListOf()
    private val steps : MutableList<Step> = mutableListOf()

    private lateinit var editName : EditText
    private lateinit var editTime : EditText; private lateinit var editKcal : EditText
    private lateinit var editDescription : EditText
    private lateinit var editType: AutoCompleteTextView
    private lateinit var textIngredients : TextView
    private lateinit var ingredientsGroup : ChipGroup
    private lateinit var editIngredients : EditText; private lateinit var editCount : EditText
    private lateinit var linearSteps : LinearLayout

    private lateinit var dataBase : MyRecipesDataBase
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.gray_light)


        init()
    }

    private fun init(){

        dataBase = MyRecipesDataBase.get(this)
        loadingDialog = LoadingDialog(this)

        editName = findViewById(R.id.edit_name)
        editTime = findViewById(R.id.edit_time); editKcal = findViewById(R.id.edit_kcal)
        editDescription = findViewById(R.id.edit_description)

        ingredientsGroup = findViewById(R.id.ingredientsGroup)
        editIngredients = findViewById(R.id.edit_ingredients); editCount = findViewById(R.id.edit_count)
        textIngredients = findViewById(R.id.text_ingredients)

        linearSteps = findViewById(R.id.linear_steps)

        editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                name = editName.text.toString(); hintColor(this@CreateActivity, editName, R.color.black) }
            override fun afterTextChanged(editable: Editable) {}
        })

        editTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                time = editTime.text.toString().toInt(); hintColor(this@CreateActivity, editTime, R.color.black) }
            override fun afterTextChanged(editable: Editable) {}
        })

        editKcal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                calories = editKcal.text.toString().toInt();  hintColor(this@CreateActivity, editKcal, R.color.black) }
            override fun afterTextChanged(editable: Editable) {}
        })

        editType = findViewById(R.id.edit_type)
        val countries: Array<out String> = resources.getStringArray(R.array.food_type)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, countries).also { adapter -> editType.setAdapter(adapter) }
        editType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                textIngredients.setTextColor(getColor(R.color.black))
                type = editType.text.toString(); setDefaultEdit(editType, this@CreateActivity) }
            override fun afterTextChanged(editable: Editable) {}
        })

        findViewById<ImageView>(R.id.image_food).setOnClickListener { createWarningDialog() }

        findViewById<LinearLayout>(R.id.linear_add).setOnClickListener{ if(checkField()){ loadingDialog.show(); save() } }

        findViewById<LinearLayout>(R.id.linear_add_step).setOnClickListener { createStepDialog() }

        findViewById<ImageView>(R.id.image_add).setOnClickListener {
            val ingredient = editIngredients.text.toString()
            val count = editCount.text.toString()
            if(ingredient.isEmpty() || count.isEmpty()){
                Toast.makeText(this, "поля пустые", Toast.LENGTH_LONG).show()
            }else{
                ingredients.add("$ingredient ⨯$count")
                editIngredients.setText(R.string.none); editCount.setText(R.string.none)
                updateIngredients()
            }
        }

    }

    private fun save(){
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val data : String = LocalDateTime.now().format(formatter)

        val recipe = Recipe(null, data, name, null, description, type, time, calories, stringList(), UserData.instance.id!!)
        lifecycleScope.launch(Dispatchers.IO) {
            val id = dataBase.dao().insertRecipe(recipe)
            for(step in steps){
                if(id != null){
                    step.recipeId = id.toInt()
                    dataBase.dao().insertStep(step)
                }

            }
            withContext(Dispatchers.Main) { loadingDialog.close(); finish() }
        }
    }

    private fun createStepDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.step_create)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.window!!.statusBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.window!!.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        dialog.setCancelable(true)

        val imageFood = dialog.findViewById<ImageView>(R.id.image_food)
        val editTime = dialog.findViewById<EditText>(R.id.edit_time)
        val editDescription = dialog.findViewById<EditText>(R.id.edit_description)
        val textEnter = dialog.findViewById<TextView>(R.id.text_enter)

        textEnter.setOnClickListener {
            val time = editTime.text.toString()
            val description = editDescription.text.toString()
            if(time.isEmpty() || description.isEmpty()){
            }else{
                val step = Step(null, description, time.toDouble(), -1)
                steps.add(step)
                updateStep()
                dialog.dismiss()
            }
        }; dialog.show()
    }

    private fun createWarningDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.create_warning_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.window!!.statusBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.window!!.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        dialog.setCancelable(true)

        dialog.findViewById<TextView>(R.id.text_delete).setOnClickListener { finish() }
        dialog.findViewById<EditText>(R.id.text_cansel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun updateIngredients(){
        ingredientsGroup.removeAllViews()
        for(ingredient in ingredients){
            val chip = LayoutInflater.from(this).inflate(R.layout.chip_element, ingredientsGroup, false) as Chip
            chip.apply {
                text = ingredient
                setOnClickListener {
                    ingredientsGroup.removeView(this);
                    ingredients.remove(ingredient);
                    updateIngredients()
                }
            }
            ingredientsGroup.addView(chip)
        }
    }

    private fun updateStep(){
        linearSteps.removeAllViews()
        for (i in 0 until steps.size) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.step_element, linearSteps, false)
            val textCount = itemView.findViewById<TextView>(R.id.text_count)
            val textTime = itemView.findViewById<TextView>(R.id.text_time)
            val textDescription = itemView.findViewById<TextView>(R.id.text_description)
            var str = (i + 1).toString()
            textCount.text = str
            str = "${steps[i].time} m"
            textTime.text = str
            textDescription.text = steps[i].description

            linearSteps.addView(itemView)
        }
    }

    private fun checkField() : Boolean {
        var value = true
        if(name.isEmpty()){
            value = false; hintColor(this@CreateActivity, editName, R.color.main_dark_extra)
        }
        if(time == 0){
            value = false; hintColor(this@CreateActivity, editTime, R.color.main_dark_extra)
        }
        if(calories == 0){
            value = false; hintColor(this@CreateActivity, editKcal, R.color.main_dark_extra)
        }
        if(type.isEmpty()){
            value = false; hintColor(this@CreateActivity, editType, R.color.main_dark_extra)
        }
        if(ingredients.isEmpty()){
            value = false; textIngredients.setTextColor(getColor(R.color.main_dark_extra))
        }
        if(steps.isEmpty()){
            value = false
        }
        return value
    }


    private fun stringList() : String{
        var str = ""
        for(i in ingredients){ str += "$i/" }
        return str
    }
}