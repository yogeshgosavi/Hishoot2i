package org.illegaller.ratabb.hishoot2i.ui.common.behavior

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.support.annotation.Keep
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import org.illegaller.ratabb.hishoot2i.R

@Keep
class FabQuickHideBehavior @JvmOverloads @Keep constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {
    private var scrollThreshold: Int = 0
    private var scrollDistance: Int = 0
    private var scrollingDirection: Int = 0
    private var scrollingTrigger: Int = 0
    private var animator: Animator? = null

    init {
        context?.let {
            val a = it.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
            scrollThreshold = a.getDimensionPixelSize(0, 0) / 2
            a.recycle()
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy > 0 && scrollingDirection != DIRECTION_UP) {
            scrollingDirection = DIRECTION_UP
            scrollDistance = 0
        } else if (dy < 0 && scrollingDirection != DIRECTION_DOWN) {
            scrollingDirection = DIRECTION_DOWN
            scrollDistance = 0
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        scrollDistance += dyConsumed
        if (scrollDistance > scrollThreshold && scrollingTrigger != DIRECTION_UP) {
            scrollingTrigger = DIRECTION_UP
            resetAnimator(child, getTargetHideValue(coordinatorLayout, child))
        } else if (scrollDistance < -scrollThreshold && scrollingTrigger != DIRECTION_DOWN) {
            scrollingTrigger = DIRECTION_DOWN
            resetAnimator(child, 0f)
        }
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        if (consumed) {
            if (velocityY > 0 && scrollingTrigger != DIRECTION_UP) {
                scrollingTrigger = DIRECTION_UP
                resetAnimator(child, getTargetHideValue(coordinatorLayout, child))
            } else if (velocityY < 0 && scrollingTrigger != DIRECTION_DOWN) {
                scrollingTrigger = DIRECTION_DOWN
                resetAnimator(child, 0F)
            }
        }
        return false
    }

    //
    private fun resetAnimator(fab: FloatingActionButton, value: Float) {
        animator?.let {
            it.cancel()
            animator = null
        }

        animator = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, value)
            .setDuration(250)
            .apply { start() }
    }

    private fun getTargetHideValue(parent: ViewGroup, target: FloatingActionButton): Float =
        (parent.height - target.top).toFloat()

    companion object {
        private const val DIRECTION_UP = 1
        private const val DIRECTION_DOWN = -1
    }
}