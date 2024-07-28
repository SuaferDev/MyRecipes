package com.suafer.myrecipes.app

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.suafer.myrecipes.R

class Viewer {

    companion object{

        /** MainActivity **/
        fun setDefaultEdit(editText : EditText, context : Context){
            editText.setBackgroundResource(R.drawable.edit_corner_black)
            editText.setTextColor(context.getColor(R.color.black))
        }

        fun showErrorEdit(context : Context, vararg editTexts: EditText){
            for (et in editTexts) {
                setErrorEdit(et, context)
            }
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