package com.suafer.myrecipes.app

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.suafer.myrecipes.R

class Viewer {

    companion object{

        fun clearAllFilter(context : Context, vararg textViews: TextView){
            for(textView in textViews) clearFilter(context, textView)
        }

        private fun clearFilter(context: Context, textView : TextView){
            textView.setBackgroundResource(R.drawable.corner_main_dark)
            textView.setTextColor(context.getColor(R.color.black))
        }

        fun setDefaultEdit(editText : EditText, context : Context){
            editText.setBackgroundResource(R.drawable.edit_corner_black)
            editText.setTextColor(context.getColor(R.color.black))
        }

        fun showErrorEdit(context : Context, vararg editTexts: EditText){
            for (et in editTexts) {
                setErrorEdit(et, context)
            }
        }

        fun hintColor(context: Context, editText: EditText, color : Int){
            editText.setHintTextColor(context.getColor(color))
        }

        private fun setErrorEdit(editText : EditText, context : Context){
            editText.setBackgroundResource(R.drawable.edit_corner_red)
            editText.setTextColor(context.getColor(R.color.red))
        }

        fun closeKeyboard(context: Context, editText: EditText) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }

    }
}