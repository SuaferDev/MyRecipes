package com.suafer.myrecipes.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.app.Viewer
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
import com.suafer.myrecipes.database.StepData
import com.suafer.myrecipes.dialog.WarningDialog


class CreateActivity : AppCompatActivity() {

    /** Параметры рецепта **/
    private var image : String? = null
    private var name = ""; private var description = ""
    private var time = 0; private var calories = 0
    private var type = ""
    private val ingredients : MutableList<String> = mutableListOf()
    private val steps : MutableList<StepData> = mutableListOf()
    private var recipeImage : Bitmap? = null
    private var stepImage : Bitmap? = null
    private var ifStepImage = true


    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2

    private lateinit var imageFood : ImageView
    private lateinit var editName : EditText
    private lateinit var editTime : EditText; private lateinit var editKcal : EditText
    private lateinit var editDescription : EditText
    private lateinit var editType: AutoCompleteTextView
    private lateinit var textIngredients : TextView
    private lateinit var ingredientsGroup : ChipGroup
    private lateinit var editIngredients : EditText; private lateinit var editCount : EditText
    private lateinit var linearSteps : LinearLayout
    private lateinit var imageFoodStep : ImageView

    private lateinit var dataBase : MyRecipesDataBase
    private lateinit var loadingDialog: LoadingDialog

    private var id : Int = -1


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

        id = intent.getIntExtra("ID_VALUE", -1)

        animateLinear()
        init()
        fillFields()
    }

    private fun init(){

        dataBase = MyRecipesDataBase.get(this)
        loadingDialog = LoadingDialog(this)

        imageFood = findViewById(R.id.image_food)
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

        editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                description = editDescription.text.toString();
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        editType = findViewById(R.id.edit_type)
        val countries: Array<out String> = resources.getStringArray(R.array.food_type)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, countries).also { adapter -> editType.setAdapter(adapter) }
        editType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                textIngredients.setTextColor(getColor(R.color.black))
                type = editType.text.toString(); hintColor(this@CreateActivity, editTime, R.color.black) }
            override fun afterTextChanged(editable: Editable) {}
        })

        findViewById<ImageView>(R.id.image_back).setOnClickListener { WarningDialog.show(this, this) }

        findViewById<ImageView>(R.id.image_food).setOnClickListener {
            ifStepImage = false
            showImageSelectionDialog()
        }

        findViewById<LinearLayout>(R.id.linear_add).setOnClickListener{ if(checkField()){ loadingDialog.show(); save() } }

        findViewById<LinearLayout>(R.id.linear_add_step).setOnClickListener {
            createStepDialog(-1)
        }

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

    private fun animateLinear(){
        val animTop = AnimationUtils.loadAnimation(this, R.anim.anim_move_up)
        val animDown = AnimationUtils.loadAnimation(this, R.anim.anim_move_top)
        findViewById<LinearLayout>(R.id.linear_top).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_description).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_ingredients).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_add).startAnimation(animTop)
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf(string(R.string.dialog_image_make_photo), string(R.string.dialog_image_select))

        AlertDialog.Builder(this)
            .setTitle(string(R.string.dialog_image_choose))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage()
                    1 -> selectImageFromGallery()
                }
            }
            .show()
    }

    private fun captureImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun selectImageFromGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { selectPictureIntent ->
            selectPictureIntent.type = "image/*"
            startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    if(ifStepImage){
                        recipeImage = imageBitmap; Viewer.setImage(imageFood, recipeImage)
                    }else{
                        stepImage = imageBitmap; Viewer.setImage(imageFoodStep, stepImage)
                    }

                }
                REQUEST_IMAGE_SELECT -> {
                    val imageUri: Uri? = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    recipeImage = imageBitmap
                    if(ifStepImage){
                        stepImage = imageBitmap; Viewer.setImage(imageFoodStep, stepImage)
                    }else{
                        recipeImage = imageBitmap; Viewer.setImage(imageFood, recipeImage)

                    }
                }
            }
        }
    }


    private fun createStepDialog(position : Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.step_create)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.window!!.statusBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.window!!.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        dialog.setCancelable(true)

        imageFoodStep = dialog.findViewById<ImageView>(R.id.image_food)
        val editTime = dialog.findViewById<EditText>(R.id.edit_time)
        val editDescription = dialog.findViewById<EditText>(R.id.edit_description)
        val textEnter = dialog.findViewById<TextView>(R.id.text_enter)

        if(position != -1){
            editTime.setText(steps[position].time().toString())
            editDescription.setText(steps[position].description())
        }



        imageFoodStep.setOnClickListener {
            ifStepImage = true
            showImageSelectionDialog()
        }

        textEnter.setOnClickListener {
            val time = editTime.text.toString()
            val description = editDescription.text.toString()
            if(time.isEmpty() || description.isEmpty()){
                Toast.makeText(this, getString(R.string.create_activity_empty), Toast.LENGTH_LONG).show()
            }else{
                if(position != -1){
                    steps[position].update(description, time.toDouble())
                }else{
                    val step = StepData(null,stepImage, description, time.toDouble())
                    steps.add(step)
                }
                updateStep()
                dialog.dismiss()
            }
        }; dialog.show()
    }


    private fun fillFields(){
        if( id != -1){
            loadingDialog.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val recipe = dataBase.dao().getRecipe(id)
                if(recipe != null){
                    name = recipe.name
                    description = recipe.description
                    time = recipe.time
                    type = recipe.type
                    calories = recipe.calories
                    ingredients.addAll(recipe.ingredients.split("/"))
                    //steps.addAll(dataBase.dao().getAllSteps(id))
                    val recipeSteps = dataBase.dao().getAllSteps(id)
                    for(step in recipeSteps){
                        steps.add(
                            StepData(
                                step.id,
                                Tool.getImage(("step_" + step.id),this@CreateActivity),
                                step.description,
                                step.time))
                    }
                }
                withContext(Dispatchers.Main) {
                    val recipeImage = Tool.getImage("recipe_$id", this@CreateActivity)
                    if(recipeImage != null){
                        Viewer.setImage(imageFood, recipeImage)
                    }
                    editName.setText(name);  editDescription.setText(description)
                    editType.setText(type)
                    editTime.setText(time.toString()); editKcal.setText(calories.toString())
                    updateIngredients(); updateStep()
                    loadingDialog.close();
                }
            }
        }
    }

    private fun save(){
        val data : String = Tool.getTime()

        val recipe = Recipe(null, data, name, description, type, time, calories, stringList(), UserData.instance.id!!)
        var resultMessage = ""
        lifecycleScope.launch(Dispatchers.IO) {
            val idRecipe = if (id != -1) {
                resultMessage = string(R.string.create_activity_update)
                recipe.id = id
                dataBase.dao().updateRecipe(recipe)
                id
            } else {
                resultMessage = string(R.string.create_activity_create)
                dataBase.dao().insertRecipe(recipe)
            }
            if(recipeImage != null){
                Tool.saveImage(recipeImage!!, "recipe_$idRecipe", this@CreateActivity)
            }

            for (step in steps) {
                val recipeStep = step.step(idRecipe!!.toInt())

                var stepId = 0
                if (step.id() == null) {
                    stepId = dataBase.dao().insertStep(recipeStep)!!.toInt()
                } else {
                    dataBase.dao().updateStep(recipeStep)
                    stepId = step.id()!!
                }
                if(step.image() != null){
                    Tool.saveImage(step.image()!!, "step_$stepId", this@CreateActivity)
                }
            }
            withContext(Dispatchers.Main) {
                loadingDialog.close()
                Toast.makeText(this@CreateActivity, resultMessage, Toast.LENGTH_LONG).show()
                val i = Intent(this@CreateActivity, SearchActivity::class.java)
                startActivity(i)
                finish()
            }
        }

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
            val imageFood = itemView.findViewById<ImageView>(R.id.image_food)
            val textDescription = itemView.findViewById<TextView>(R.id.text_description)
            var str = (i + 1).toString()
            textCount.text = str
            str = "${steps[i].time()} m"
            textTime.text = str
            if(steps[i].image() != null){
                imageFood.setImageBitmap(steps[i].image())
                imageFood.setBackgroundColor(getColor(R.color.none))
                imageFood.scaleType = ImageView.ScaleType.CENTER_CROP
                imageFood.setPadding(0,0,0,0)
            }
            textDescription.text = steps[i].description()
            linearSteps.setOnClickListener { createStepDialog(i) }

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

    private fun string(i : Int) : String{ return getString(i) }
}



/*

class CreateActivity : AppCompatActivity() {

    /** Параметры рецепта **/
    private var image : String? = null
    private var name = ""; private var description = ""
    private var time = 0; private var calories = 0
    private var type = ""
    private val ingredients : MutableList<String> = mutableListOf()
    private val steps : MutableList<StepData> = mutableListOf()
    private var recipeImage : Bitmap? = null


    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_SELECT = 2

    private lateinit var imageFood : ImageView
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

    private var id : Int = -1


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

        id = intent.getIntExtra("ID_VALUE", -1)

        animateLinear()
        init()
        fillFields()
    }

    private fun init(){

        dataBase = MyRecipesDataBase.get(this)
        loadingDialog = LoadingDialog(this)

        imageFood = findViewById(R.id.image_food)
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

        editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                description = editDescription.text.toString();
            }
            override fun afterTextChanged(editable: Editable) {}
        })

        editType = findViewById(R.id.edit_type)
        val countries: Array<out String> = resources.getStringArray(R.array.food_type)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, countries).also { adapter -> editType.setAdapter(adapter) }
        editType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                textIngredients.setTextColor(getColor(R.color.black))
                type = editType.text.toString(); hintColor(this@CreateActivity, editTime, R.color.black) }
            override fun afterTextChanged(editable: Editable) {}
        })

        findViewById<ImageView>(R.id.image_back).setOnClickListener { WarningDialog.show(this, this) }

        findViewById<ImageView>(R.id.image_food).setOnClickListener { showImageSelectionDialog() }

        findViewById<LinearLayout>(R.id.linear_add).setOnClickListener{ if(checkField()){ loadingDialog.show(); save() } }

        findViewById<LinearLayout>(R.id.linear_add_step).setOnClickListener { createStepDialog(-1) }

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

    private fun animateLinear(){
        val animTop = AnimationUtils.loadAnimation(this, R.anim.anim_move_up)
        val animDown = AnimationUtils.loadAnimation(this, R.anim.anim_move_top)
        findViewById<LinearLayout>(R.id.linear_top).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_description).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_ingredients).startAnimation(animDown)
        findViewById<LinearLayout>(R.id.linear_add).startAnimation(animTop)
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf(string(R.string.dialog_image_make_photo), string(R.string.dialog_image_select))

        AlertDialog.Builder(this)
            .setTitle(string(R.string.dialog_image_choose))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage()
                    1 -> selectImageFromGallery()
                }
            }
            .show()
    }

    private fun captureImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun selectImageFromGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { selectPictureIntent ->
            selectPictureIntent.type = "image/*"
            startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    recipeImage = imageBitmap
                    if(recipeImage != null){ Viewer.setImage(imageFood, recipeImage!!) }
                }
                REQUEST_IMAGE_SELECT -> {
                    val imageUri: Uri? = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    recipeImage = imageBitmap
                    if(recipeImage != null){ Viewer.setImage(imageFood, recipeImage!!) }
                }
            }
        }
    }
    private fun createStepDialog(position : Int) {
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

        if(position != -1){
            editTime.setText(steps[position].time().toString())
            editDescription.setText(steps[position].description())
        }



        imageFood.setOnClickListener {

        }

        textEnter.setOnClickListener {
            val time = editTime.text.toString()
            val description = editDescription.text.toString()
            if(time.isEmpty() || description.isEmpty()){
                Toast.makeText(this, getString(R.string.create_activity_empty), Toast.LENGTH_LONG).show()
            }else{
                if(position != -1){
                    steps[position].update(description, time.toDouble())
                }else{
                    val step = StepData(null,null, description, time.toDouble())
                    steps.add(step)
                }
                updateStep()
                dialog.dismiss()
            }
        }; dialog.show()
    }


    private fun fillFields(){
        if( id != -1){
            loadingDialog.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val recipe = dataBase.dao().getRecipe(id)
                if(recipe != null){
                    name = recipe.name
                    description = recipe.description
                    time = recipe.time
                    type = recipe.type
                    calories = recipe.calories
                    ingredients.addAll(recipe.ingredients.split("/"))
                    //steps.addAll(dataBase.dao().getAllSteps(id))
                    val recipeSteps = dataBase.dao().getAllSteps(id)
                    for(step in recipeSteps){
                        steps.add(
                            StepData(
                                step.id,
                                Tool.getImage(("step_" + step.id),this@CreateActivity),
                                step.description,
                                step.time))
                    }
                }
                withContext(Dispatchers.Main) {
                    val recipeImage = Tool.getImage("recipe_$id", this@CreateActivity)
                    if(recipeImage != null){
                        Viewer.setImage(imageFood, recipeImage)
                    }
                    editName.setText(name);  editDescription.setText(description)
                    editType.setText(type)
                    editTime.setText(time.toString()); editKcal.setText(calories.toString())
                    updateIngredients(); updateStep()
                    loadingDialog.close();
                }
            }
        }
    }

    private fun save(){
        val data : String = Tool.getTime()

        val recipe = Recipe(null, data, name, description, type, time, calories, stringList(), UserData.instance.id!!)
        var resultMessage = ""
        lifecycleScope.launch(Dispatchers.IO) {
            val idRecipe = if (id != -1) {
                resultMessage = string(R.string.create_activity_update)
                recipe.id = id
                dataBase.dao().updateRecipe(recipe)
                id
            } else {
                resultMessage = string(R.string.create_activity_create)
                dataBase.dao().insertRecipe(recipe)
            }
            if(recipeImage != null){
                Tool.saveImage(recipeImage!!, "recipe_$idRecipe", this@CreateActivity)
            }

            for (step in steps) {
                val recipeStep = step.step(idRecipe!!.toInt())
                if (step.id() == null) {
                    dataBase.dao().insertStep(recipeStep)
                } else {
                    dataBase.dao().updateStep(recipeStep)
                }
            }
            withContext(Dispatchers.Main) {
                loadingDialog.close()
                Toast.makeText(this@CreateActivity, resultMessage, Toast.LENGTH_LONG).show()
                val i = Intent(this@CreateActivity, SearchActivity::class.java)
                startActivity(i)
                finish()
            }
        }

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
            val imageFood = itemView.findViewById<ImageView>(R.id.image_food)
            val textDescription = itemView.findViewById<TextView>(R.id.text_description)
            var str = (i + 1).toString()
            textCount.text = str
            str = "${steps[i].time()} m"
            textTime.text = str
            if(steps[i].image() != null){
                imageFood.setImageBitmap(steps[i].image())
                imageFood.setBackgroundColor(getColor(R.color.none))
                imageFood.scaleType = ImageView.ScaleType.CENTER_CROP
                imageFood.setPadding(0,0,0,0)
            }
            textDescription.text = steps[i].description()
            linearSteps.setOnClickListener { createStepDialog(i) }

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

    private fun string(i : Int) : String{ return getString(i) }
}
 */