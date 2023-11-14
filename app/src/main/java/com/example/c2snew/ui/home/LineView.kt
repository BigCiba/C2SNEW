package com.example.c2snew.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint()
    private var lineHeight = 0f
    private var lineWidth = 0f

    init {
        paint.color = resources.getColor(android.R.color.holo_red_light) // 设置线的颜色
        paint.strokeWidth = 2f // 设置线的宽度
    }
    fun setLineCoordinates(height: Float, width:Float) {
        this.lineHeight = height
        this.lineWidth = width
        invalidate() // 通知 View 重绘
    }
    override fun onDraw(canvas: Canvas) {
        if (this.lineHeight != 0f) {
            super.onDraw(canvas)
            val top = 400f.coerceAtMost((lineHeight - lineWidth) / 400 * height)
            val bottom = 400f.coerceAtMost((lineHeight + lineWidth) / 400 * height)
            canvas?.drawLine(0f, top, width.toFloat(), top, paint)
            canvas?.drawLine(0f, bottom, width.toFloat(), bottom, paint)
//            canvas?.drawRect(Rect(0, (height - lineWidth).toInt(), width,(height + lineWidth).toInt()), paint)
        }
    }
}
