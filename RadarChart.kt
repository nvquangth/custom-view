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
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

class RadarChart(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        const val VALUE_MAX = 100f
        const val VALUE_MIN = 0f
        const val COLOR_DEFAULT = Color.BLACK
        const val RADIUS_DEFAULT = 4f
        const val WIDTH_DEFAULT = 2f
    }

    private var a = VALUE_MIN
    private var b = VALUE_MIN
    private var c = VALUE_MIN
    private var dotColor = COLOR_DEFAULT
    private var dotRadius = RADIUS_DEFAULT
    private var lineColor = COLOR_DEFAULT
    private var lineWidth = WIDTH_DEFAULT
//    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val paint = Paint()

    init {
        setupAttributes(attrs)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val k = dotRadius
        val radius = width / 2f - k

        val xA = (1 - a * sqrt(3f) / 200) * radius + k
        val yA =
            (2 * sqrt(2f) * sin(PI / 12).toFloat() / sqrt(3f) + 1 / sqrt(3f) - a / 200) * radius + k * (2 * sqrt(
                2f
            ) * sin(
                PI / 12
            ).toFloat() + 1) / sqrt(3f)

        val xB = (1 + b * sqrt(3f) / 200) * radius + k
        val yB =
            (2 * sqrt(2f) * sin(PI / 12).toFloat() / sqrt(3f) + 1 / sqrt(3f) - b / 200) * radius + k * (2 * sqrt(
                2f
            ) * sin(
                PI / 12
            ).toFloat() + 1) / sqrt(3f)

        val xC = radius + k
        val yC = (1 + c / 100) * radius + k

        paint.color = lineColor
        paint.strokeWidth = lineWidth

        canvas?.drawCircle(xA, yA, dotRadius, paint)
        canvas?.drawCircle(xB, yB, dotRadius, paint)
        canvas?.drawCircle(xC, yC, dotRadius, paint)

        paint.color = dotColor
        canvas?.drawLine(xA, yA, xB, yB, paint)
        canvas?.drawLine(xB, yB, xC, yC, paint)
        canvas?.drawLine(xC, yC, xA, yA, paint)
    }

    fun setData(aValue: Float?, bValue: Float?, cValue: Float?) {
        if (aValue == null || bValue == null || cValue == null) return

        val desA = if (aValue > VALUE_MAX || aValue < VALUE_MIN) VALUE_MIN else aValue
        val desB = if (bValue > VALUE_MAX || bValue < VALUE_MIN) VALUE_MIN else bValue
        val desC = if (cValue > VALUE_MAX || cValue < VALUE_MIN) VALUE_MIN else cValue

        val n = 5
        val delayTime = 40L

        val kA: Float = (desA - a) / n
        val kB: Float = (desB - b) / n
        val kC: Float = (desC - c) / n

//        coroutineScope.launch {
//            repeat(n - 1) {
//                a += kA
//                b += kB
//                c += kC
//                invalidate()
//                delay(delayTime)
//            }
//
//            a = desA
//            b = desB
//            c = desC
//            invalidate()
//        }

        a = desA
        b = desB
        c = desC
        invalidate()
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val type =
            context.theme.obtainStyledAttributes(attrs, R.styleable.RadarChart, 0, 0)
        dotColor = type.getColor(R.styleable.RadarChart_rcDotColor, COLOR_DEFAULT)
        dotRadius = type.getDimension(R.styleable.RadarChart_rcDotRadius, RADIUS_DEFAULT)
        lineColor = type.getColor(R.styleable.RadarChart_rcLineColor, COLOR_DEFAULT)
        lineWidth = type.getDimension(R.styleable.RadarChart_rcLineWidth, WIDTH_DEFAULT)
        type.recycle()
    }
}
