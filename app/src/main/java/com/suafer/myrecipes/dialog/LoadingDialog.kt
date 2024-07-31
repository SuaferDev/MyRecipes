package com.suafer.myrecipes.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.suafer.myrecipes.R

class LoadingDialog(context: Context) {
    private val dialogLoading: Dialog = Dialog(context).apply {
        setContentView(R.layout.loading_dialog)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.windowAnimations = R.style.DialogLoadingAnim
        setCancelable(false)

        val imageLoading = findViewById<ImageView>(R.id.imageLoading)

        val rotateAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = Animation.INFINITE

        imageLoading.startAnimation(rotateAnimation)
    }

    fun show() { dialogLoading.show() }

    fun close() { dialogLoading.dismiss() }
}
