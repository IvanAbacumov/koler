package com.chooloo.www.callmanager.listener

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView
import com.chooloo.www.callmanager.util.Utilities
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber
import java.util.*

//TODO move to a new folder called 'listener'
class LongClickOptionsListener(private val mContext: Context,
                               private val mFabView: ViewGroup,
                               private val mOnNormalClick: View.OnClickListener,
                               private val mOverlayChangeListener: OverlayChangeListener) : OnTouchListener {
    private var mIsCanceled = false
    private val mRunnable = LongClickRunnable()
    private val mHandler = Handler()

    // Lists
    private val mFloatingButtons: MutableList<FloatingActionButton> = ArrayList()
    private val mActionsText: MutableList<TextView> = ArrayList()
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mIsCanceled = false // This is no longer canceled

                // Start the timer for long click
                if (!mRunnable.isFinished) {
                    mHandler.postDelayed(mRunnable, LONG_PRESS_TIMEOUT)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val outRect = Rect()
                v.getDrawingRect(outRect)
                val x = event.x.toInt()
                val y = event.y.toInt()

                // If pointer not inside the button
                if (!outRect.contains(x, y)) {
                    val rawX = event.rawX.toInt()
                    val rawY = event.rawY.toInt()

                    // Cycle through each action button and check if the pointer is ints vicinity
                    for (action in mFloatingButtons) {
                        if (Utilities.inViewInBounds(action, rawX, rawY, 8)) {
                            highlightFAB(action, true)
                        } else {
                            highlightFAB(action, false)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val rawX = event.rawX.toInt()
                val rawY = event.rawY.toInt()
                var actionPerformed = false
                // Cycle through each action button and check if the pointer is in its vicinity
                for (action in mFloatingButtons) {
                    if (Utilities.inViewInBounds(action, rawX, rawY, 16)) {
                        action.performClick()
                        actionPerformed = true
                    }
                }

                // Perform a normal click if this wasn't a long click
                if (!actionPerformed && !mRunnable.isFinished && Utilities.inViewInBounds(v, rawX, rawY, 0)) {
                    mOnNormalClick.onClick(v)
                }
                cancel() //Don't run code in the long click related runnable
            }
        }
        return false
    }

    /**
     * Cancel the long click system
     */
    private fun cancel() {
        mIsCanceled = true
        mRunnable.reset()
        changeVisibility(false)
    }

    /**
     * Change the visibility of the overlay
     *
     * @param overlayVisible whether to show the overlay or not
     */
    private fun changeVisibility(overlayVisible: Boolean) {
        if (overlayVisible) {
            if (mOverlayChangeListener.setOverlay(mFabView)) {
                mRunnable.isFinished = true
                Utilities.vibrate(mContext)
            }
        } else {
            mOverlayChangeListener.removeOverlay(mFabView)
            for (textView in mActionsText) {
                textView.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Highlight or unhighlight a given button
     *
     * @param actionButton the button to highlight or unhighlight
     * @param highlight    whether to highlight
     */
    private fun highlightFAB(actionButton: FloatingActionButton, highlight: Boolean) {
        actionButton.isHovered = highlight

        //Find the TextView correlated with this button
        val actionIndex = mFloatingButtons.indexOf(actionButton)
        if (actionIndex == -1 || mActionsText.size <= actionIndex) Timber.w("Couldn't find the TextView correlated with action button in index %d", actionIndex)
        val actionText = mActionsText[actionIndex]
        if (highlight) actionText.visibility = View.VISIBLE else actionText.visibility = View.INVISIBLE
    }

    // -- Classes -- //
    internal inner class LongClickRunnable : Runnable {
        var isFinished = false
        fun reset() {
            isFinished = false
        }

        override fun run() {
            if (!mIsCanceled) {
                changeVisibility(true)
            }
        }
    }

    interface OverlayChangeListener {
        /**
         * Set the given view as an overlay
         *
         * @param view the overlay
         * @return whether the view has been set as an overlay
         */
        fun setOverlay(view: ViewGroup): Boolean

        /**
         * Remove the given view
         *
         * @param view the overlay
         */
        fun removeOverlay(view: ViewGroup)
    }

    companion object {
        // Final variables
        private val LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout().toLong()
    }

    /**
     * Constructor
     *
     * @param context
     * @param fabView
     * @param onNormalClick
     * @param overlayChangeListener
     */
    init {
        for (i in 0 until mFabView.childCount) {
            val v = mFabView.getChildAt(i)
            if (v is FloatingActionButton) {
                mFloatingButtons.add(v)
            }
            if (v is TextView) {
                mActionsText.add(v)
            }
        }
    }
}