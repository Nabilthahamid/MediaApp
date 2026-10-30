package com.example.mediaapp

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView

class ZoomImageView(context: Context) : AppCompatImageView(context) {

    private var zoom = 1f

    private val detector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(
                detector: ScaleGestureDetector
            ): Boolean {
                zoom = (zoom * detector.scaleFactor).coerceIn(1f, 5f)
                invalidate()
                return true
            }
        }
    )

    init {
        scaleType = ScaleType.FIT_CENTER
        contentDescription = "Internet image. Pinch with two fingers to zoom."
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        val checkpoint = canvas.save()

        canvas.scale(
            zoom,
            zoom,
            width / 2f,
            height / 2f
        )

        super.onDraw(canvas)
        canvas.restoreToCount(checkpoint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                performClick()
            }

            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun resetZoom() {
        zoom = 1f
        invalidate()
    }
}
