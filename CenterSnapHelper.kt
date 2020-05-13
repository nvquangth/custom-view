import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CenterSnapHelper : LinearSnapHelper() {

    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var scrolled = false
    private var recyclerView: RecyclerView? = null
    var onScrollToPosition: ((View, Int) -> Unit)? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            scrolled = if (newState == RecyclerView.SCROLL_STATE_IDLE && scrolled) {
                recyclerView.layoutManager?.let { layoutManager ->
                    val view = findSnapView(layoutManager)
                    if (view != null) {
                        val out = calculateDistanceToFinalSnap(layoutManager, view)
                        if (out != null) {
                            recyclerView.smoothScrollBy(out[0], out[1])
                        }
                        onScrollToPosition?.invoke(view, layoutManager.getPosition(view))
                    }
                }
                false
            } else {
                true
            }
        }
    }

    fun scrollTo(position: Int, smooth: Boolean) {
        recyclerView?.layoutManager?.let { layoutManager ->

            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)
            if (viewHolder != null) {
                val distances = calculateDistanceToFinalSnap(layoutManager, viewHolder.itemView)
                distances?.let {
                    if (smooth) {
                        recyclerView?.smoothScrollBy(it[0], it[1])
                    } else {
                        recyclerView?.scrollBy(it[0], it[1])
                    }
                }

            } else {
                if (smooth) {
                    recyclerView?.smoothScrollToPosition(position)
                } else {
                    recyclerView?.scrollToPosition(position)
                }
            }
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        if (layoutManager == null) {
            return null
        }
        if (layoutManager.canScrollVertically()) {
            val vHelper = getVerticalHelper(layoutManager)
            if (vHelper != null) {
                return findCenterView(layoutManager, vHelper)
            }
        } else if (layoutManager.canScrollHorizontally()) {
            val hHelper = getHorizontalHelper(layoutManager)
            if (hHelper != null) {
                return findCenterView(layoutManager, hHelper)
            }
        }
        return null
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        val hHelper = getHorizontalHelper(layoutManager)
        if (layoutManager.canScrollHorizontally() && hHelper != null) {
            out[0] = distanceToCenter(layoutManager, targetView, hHelper)
        }
        val vHelper = getVerticalHelper(layoutManager)
        if (layoutManager.canScrollVertically() && vHelper != null) {
            out[1] = distanceToCenter(layoutManager, targetView, vHelper)
        }
        return out
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        val center: Int = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        var absClosest = Integer.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i) ?: return closestChild
            val childCenter = if (helper == horizontalHelper) {
                (child.x + child.width / 2).toInt()
            } else {
                (child.y + child.height / 2).toInt()
            }
            val absDistance = abs(childCenter - center)

            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childCenter = if (helper == horizontalHelper) {
            (targetView.x + targetView.width / 2).toInt()
        } else {
            (targetView.y + targetView.height / 2).toInt()
        }
        val containerCenter = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }
        return childCenter - containerCenter
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (verticalHelper == null || verticalHelper?.layoutManager != layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper? {
        if (horizontalHelper == null || horizontalHelper?.layoutManager != layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper
    }
}
