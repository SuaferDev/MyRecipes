package com.suafer.myrecipes.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.suafer.myrecipes.R
import com.suafer.myrecipes.app.Constant.Companion.PADDING_CATEGORY_SIDE
import com.suafer.myrecipes.app.Constant.Companion.PADDING_CATEGORY_TOP
import com.suafer.myrecipes.database.Recipe


class CustomRecipeAdapter(private val context: Activity, arr: List<Recipe>) :
    ArrayAdapter<Any?>(context, R.layout.recipe_element, arr as List<Any?>) {

    private val arr : List<Recipe>
    private val height = 140

    init { this.arr = arr}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater

        val view: View = inflater.inflate(R.layout.recipe_element, null, true)

        val linear = view.findViewById<LinearLayout>(R.id.linear)
        val imageFood = view.findViewById<ImageView>(R.id.image_food)
        val textName = view.findViewById<TextView>(R.id.text_name)
        val textTime = view.findViewById<TextView>(R.id.text_time)
        val textKcal = view.findViewById<TextView>(R.id.text_kcal)

        if(position == 0) linear.setPadding(PADDING_CATEGORY_SIDE, height, PADDING_CATEGORY_SIDE,0)
        else{
            if(position == arr.size - 1) linear.setPadding(PADDING_CATEGORY_SIDE, PADDING_CATEGORY_TOP, PADDING_CATEGORY_SIDE, height)
            else{
                linear.setPadding(PADDING_CATEGORY_SIDE, PADDING_CATEGORY_TOP, PADDING_CATEGORY_SIDE,0)
            }
        }
        //imageFood.setImageResource()
        textName.text = arr[position].name
        val str = arr[position].time.toString() + " m"
        textTime.text = str
        textKcal.text = arr[position].calories.toString()

        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_move_up)
        view.startAnimation(animation)
        return view
    }
}