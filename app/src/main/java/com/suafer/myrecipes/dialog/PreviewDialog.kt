package com.suafer.myrecipes.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.suafer.myrecipes.R
import com.suafer.myrecipes.activity.CreateActivity
import com.suafer.myrecipes.activity.RecipeActivity
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.app.Viewer
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.database.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewDialog {
   companion object{
       fun show(context: Context, recipe : Recipe){
           val dialog = Dialog(context)
           dialog.setContentView(R.layout.recipe_preview)
           dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
           dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
           dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
           dialog.window!!.statusBarColor = ContextCompat.getColor(context, R.color.background)
           dialog.window!!.navigationBarColor = ContextCompat.getColor(context, R.color.background)
           dialog.setCancelable(true)

           dialog.findViewById<TextView>(R.id.text_name).text = recipe.name
           dialog.findViewById<TextView>(R.id.text_time).text = recipe.time.toString()
           dialog.findViewById<TextView>(R.id.text_kcal).text = recipe.calories.toString()
           dialog.findViewById<TextView>(R.id.text_description).text = recipe.description

           val imageFood = dialog.findViewById<ImageView>(R.id.image_food)
           val recipeImage = Tool.getImage("recipe_" + recipe.id, context)
           if(recipeImage == null){
               imageFood.scaleType = ImageView.ScaleType.CENTER
               imageFood.setBackgroundResource(R.drawable.background_new_step)
               imageFood.setImageResource(R.drawable.icon_no_image)
           }else{
               imageFood.setImageBitmap(recipeImage)
           }

           Viewer.createIngredients(
               dialog.findViewById(R.id.ingredientsGroup),
               recipe.ingredients.split("/"),
               context
           )
           dialog.findViewById<TextView>(R.id.button_detail).setOnClickListener {
              val i = Intent(context, RecipeActivity::class.java)
               i.putExtra("ID_VALUE", recipe.id!!)
               context.startActivity(i)
               dialog.dismiss()
           }

           dialog.findViewById<ImageView>(R.id.image_close).setOnClickListener { dialog.dismiss() }

           dialog.findViewById<ImageView>(R.id.image_edit).setOnClickListener {
               val i = Intent(context, CreateActivity::class.java).apply {
                   putExtra("ID_VALUE", recipe.id)
               }
               context.startActivity(i)
               dialog.dismiss()
           }

           dialog.show()

       }
   }
}