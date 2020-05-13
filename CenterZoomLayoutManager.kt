import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CenterZoomLayoutManager(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean
) :
    LinearLayoutManager(context, orientation, reverseLayout) {
    private val shrinkAmount = 0.15f
    private val shrinkDistance = 0.9f
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = shrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - shrinkAmount
            for (i in 0 until childCount) {
                val child: View? = getChildAt(i)

                var childMidpoint = 0f
                child?.let {
                    childMidpoint = (getDecoratedRight(it) + getDecoratedLeft(it)) / 2f
                }

                val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child?.scaleX = scale
                child?.scaleY = scale
            }
            scrolled
        } else 0
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        super.onLayoutChildren(recycler, state)
        scrollHorizontallyBy(0, recycler, state)
    }
}
