import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import jp.co.shiseido.cledepeaubeaute.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlideChart(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        const val VALUE_MAX = 100f
        const val VALUE_MIN = 0f
        const val COLOR_DEFAULT = Color.BLACK
        const val RADIUS_DEFAULT = 10f
        const val WIDTH_DEFAULT = 5f
    }

    private var progress = VALUE_MIN
    private var dotColor = COLOR_DEFAULT
    private var dotRadius = RADIUS_DEFAULT
    private var lineColor = COLOR_DEFAULT
    private var lineWidth = WIDTH_DEFAULT
//    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val paint = Paint()

    init {
        setupAttributes(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(width, (dotRadius * 2).toInt())
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val k = dotRadius
        paint.color = lineColor
        paint.strokeWidth = lineWidth

        val xA = k
        val yA = k
        val xB = width - k
        val yB = k
        canvas?.drawLine(xA, yA, xB, yB, paint)

        paint.color = dotColor
        val xC = k + progress * (width - 2 * k) / VALUE_MAX
        val yC = k
        canvas?.drawCircle(xC, yC, dotRadius, paint)
    }

    fun setData(pro: Float?) {
        if (pro == null) return

        val des = if (pro > VALUE_MAX || pro < VALUE_MIN) VALUE_MIN else pro

        val n = 5
        val delayTime = 40L

        val k: Float = (des - progress) / n

//        coroutineScope.launch {
//            repeat(n - 1) {
//                progress += k
//                invalidate()
//                delay(delayTime)
//            }
//            progress = des
//            invalidate()
//        }
        progress = des
        invalidate()
    }

    private fun setupAttributes(context: Context?, attrs: AttributeSet?) {
        val type =
            context?.theme?.obtainStyledAttributes(attrs, R.styleable.SlideChart, 0, 0) ?: return
        dotColor = type.getColor(R.styleable.SlideChart_scDotColor, COLOR_DEFAULT)
        dotRadius = type.getDimension(R.styleable.SlideChart_scDotRadius, RADIUS_DEFAULT)
        lineColor = type.getColor(R.styleable.SlideChart_scLineColor, COLOR_DEFAULT)
        lineWidth = type.getDimension(R.styleable.SlideChart_scLineWidth, WIDTH_DEFAULT)
        type.recycle()
    }
}
