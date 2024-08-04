package com.suafer.myrecipes.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.suafer.myrecipes.R
import com.suafer.myrecipes.activity.SearchActivity

class WarningDialog {
    companion object{
        fun show(context: Context, activity: Activity){
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.create_warning_dialog)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
            dialog.window!!.statusBarColor = ContextCompat.getColor(context, R.color.background)
            dialog.window!!.navigationBarColor = ContextCompat.getColor(context, R.color.white)
            dialog.setCancelable(true)

            dialog.findViewById<TextView>(R.id.text_delete).setOnClickListener {
                val i = Intent(context, SearchActivity::class.java)
                context.startActivity(i)
                activity.finish()
            }
            dialog.findViewById<TextView>(R.id.text_cansel).setOnClickListener { dialog.dismiss() }

            dialog.show()
        }
    }
}