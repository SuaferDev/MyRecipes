package com.suafer.myrecipes.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suafer.myrecipes.R
import com.suafer.myrecipes.database.Step

class CustomStepAdapter(private val steps: List<Step>) : RecyclerView.Adapter<CustomStepAdapter.StepViewHolder>() {

    var height: Int = 0
        private set

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCount = itemView.findViewById<TextView>(R.id.text_count)
        val textTime = itemView.findViewById<TextView>(R.id.text_time)
        val textDescription = itemView.findViewById<TextView>(R.id.text_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.step_element, parent, false)
        return StepViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.textCount.text = (position + 1).toString()
        holder.textTime.text = "${step.time} m"
        holder.textDescription.text = step.description

        // Measure the itemView to get its height
        holder.itemView.post {
            height += holder.itemView.height
        }
    }

    override fun getItemCount(): Int {
        return steps.size
    }
}

