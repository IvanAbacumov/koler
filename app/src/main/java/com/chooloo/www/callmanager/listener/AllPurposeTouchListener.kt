package com.chooloo.www.callmanager.listener

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

open class AllPurposeTouchListener(ctx: Context?) : OnTouchListener {
    // Constants
    private val mGestureDetector: GestureDetector
    private val mGestureListener: GestureListener
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mGestureListener.setView(v)
        return mGestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private var mView: View? = null
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return this@AllPurposeTouchListener.onSingleTapConfirmed(mView)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return this@AllPurposeTouchListener.onSingleTapUp(mView)
        }

        override fun onLongPress(e: MotionEvent) {
            this@AllPurposeTouchListener.onLongPress(mView)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {

                // The difference in the Y position
                val diffY = e2.y - e1.y
                // The difference in the X position
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // The fling is horizontal
                    if (Math.abs(diffX) > Companion.SWIPE_THRESHOLD && Math.abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        // The fling is actually a fling
                        if (diffX > 0) {
                            // The fling is to the right (the difference in the position is positive)
                            onSwipeRight()
                        } else {
                            // The fling is to the left (the difference in the position is negative)
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > Companion.SWIPE_THRESHOLD && Math.abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                    // The fling is vertical and is actually a fling
                    if (diffY > 0) {
                        // The fling is downwards
                        onSwipeBottom()
                    } else {
                        // The fling is upwards
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        fun setView(view: View?) {
            mView = view
        }




    }

    companion object {

        // Constants
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    /**
     * Notified when a single-tap occurs.
     *
     *
     * Unlike [.onSingleTapUp], this
     * will only be called after the detector is confident that the user's
     * first tap is not followed by a second tap leading to a double-tap
     * gesture.
     *
     * @param v The view the tap occurred on.
     */
    fun onSingleTapConfirmed(v: View?): Boolean {
        return false
    }

    /**
     * Notified when a tap occurs.
     *
     * @param v The view the tap occurred on.
     */
    fun onSingleTapUp(v: View?): Boolean {
        return false
    }

    fun onLongPress(v: View?) {}

    /**
     * If the user swipes right
     */
    open fun onSwipeRight() {}

    /**
     * If the user swipes left
     */
    open fun onSwipeLeft() {}

    /**
     * If the user swipes up
     */
    open fun onSwipeTop() {}

    /**
     * Guess what? if the user swipes down
     */
    open fun onSwipeBottom() {}

    /**
     * Constructor
     *
     * @param ctx
     */
    init {
        mGestureListener = GestureListener()
        mGestureDetector = GestureDetector(ctx, mGestureListener)
    }
}