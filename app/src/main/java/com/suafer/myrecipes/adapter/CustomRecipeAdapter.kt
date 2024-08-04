package com.suafer.myrecipes.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.suafer.myrecipes.R
import com.suafer.myrecipes.app.Constant.Companion.PADDING_CATEGORY_LEFT
import com.suafer.myrecipes.app.Constant.Companion.PADDING_CATEGORY_SIDE
import com.suafer.myrecipes.app.Constant.Companion.PADDING_CATEGORY_TOP
import com.suafer.myrecipes.app.Tool
import com.suafer.myrecipes.database.Recipe
import com.suafer.myrecipes.dialog.PreviewDialog

class CustomRecipeAdapter(private val context: Activity, private val arr: List<Recipe>) :
    RecyclerView.Adapter<CustomRecipeAdapter.ViewHolder>() {

    private val height = 100

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linear: ConstraintLayout = itemView.findViewById(R.id.root_linear)
        val imageFood: ImageView = itemView.findViewById(R.id.image_food)
        val textName: TextView = itemView.findViewById(R.id.text_name)
        val textTime: TextView = itemView.findViewById(R.id.text_time)
        val textKcal: TextView = itemView.findViewById(R.id.text_kcal)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val recipe = arr[position]
                    PreviewDialog.show(context, recipe)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recipe_element, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0) {
            holder.linear.setPadding(PADDING_CATEGORY_SIDE, height, PADDING_CATEGORY_SIDE, PADDING_CATEGORY_TOP)
        } else {
            if (position == arr.size - 1) {
                holder.linear.setPadding(PADDING_CATEGORY_LEFT, PADDING_CATEGORY_TOP, PADDING_CATEGORY_LEFT, height)
            } else {
                holder.linear.setPadding(PADDING_CATEGORY_LEFT, PADDING_CATEGORY_TOP, PADDING_CATEGORY_LEFT, PADDING_CATEGORY_TOP)
            }
        }

        val recipeImage = Tool.getImage("recipe_" + arr[position].id, context)
        if (recipeImage != null) {
            holder.imageFood.setImageBitmap(recipeImage)
        } else {
            holder.imageFood.setImageResource(R.drawable.icon_no_image)
            holder.imageFood.setBackgroundResource(R.drawable.background_new_step)
        }

        holder.textName.text = arr[position].name
        val str = arr[position].time.toString() + " m"
        holder.textTime.text = str
        holder.textKcal.text = arr[position].calories.toString()

        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_move_top)
        holder.itemView.startAnimation(animation)
    }

    fun getID(position : Int) : Int{
        return arr[position].id!!
    }

    override fun getItemCount(): Int {
        return arr.size
    }

}
