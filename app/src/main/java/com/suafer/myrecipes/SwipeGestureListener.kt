package com.suafer.myrecipes
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeGestureListener(
    context: Context,
    private val swipeListener: OnSwipeListener
) : View.OnTouchListener {

    interface OnSwipeListener {
        fun onSwipeUp()
        fun onSwipeDown()
    }

    private val gestureDetector = GestureDetector(context, GestureListener())

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false
            val diffY = e2.y - e1.y
            if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    swipeListener.onSwipeDown()
                } else {
                    swipeListener.onSwipeUp()
                }
                return true
            }
            return false
        }
    }
}
