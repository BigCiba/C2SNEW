package com.example.c2snew.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint()
    private var height = 100f

    init {
        paint.color = resources.getColor(android.R.color.holo_red_light) // 设置线的颜色
        paint.strokeWidth = 2f // 设置线的宽度
    }
    fun setLineCoordinates(height: Float) {
        this.height = height
        invalidate() // 通知 View 重绘
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawLine(0f, height, width.toFloat(), height, paint)
    }
}
