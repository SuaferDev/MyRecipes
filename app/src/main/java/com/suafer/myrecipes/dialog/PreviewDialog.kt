package com.suafer.myrecipes.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.suafer.myrecipes.R
import com.suafer.myrecipes.database.Recipe

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
   }
}