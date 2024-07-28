package com.suafer.myrecipes.app

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.suafer.myrecipes.R

class PopupWindowError(private val activity: Activity) {

    fun show(text: String) {
        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_error, null)

        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val textView: TextView = popupView.findViewById(R.id.textMessage)
        textView.text = text

        popupWindow.animationStyle = R.style.DialogAnimationTop
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.TOP, 0, 0)

        val linearMessage: LinearLayout = popupView.findViewById(R.id.linearMessage)
        linearMessage.visibility = View.VISIBLE

        linearMessage.setOnClickListener { popupWindow.dismiss() }
    }
}
