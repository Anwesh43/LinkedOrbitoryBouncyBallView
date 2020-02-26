package com.anwesh.uiprojects.orbitorybouncyballview

/**
 * Created by anweshmishra on 26/02/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val deg : Float = 30f
val rFactor : Float = 5f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawOrbitoryBouncyBallView(scale : Float, size : Float, paint : Paint) {
    val r : Float = size / rFactor
    val sf1 : Float = scale.sinify().divideScale(0, 2)
    val sf2 : Float = scale.sinify().divideScale(1, 2)
    paint.style = Paint.Style.STROKE
    drawCircle(0f, 0f, size, paint)
    save()
    rotate(deg * sf2)
    paint.style = Paint.Style.FILL
    drawCircle((size + r) * sf1, 0f, r, paint)
    restore()
}

fun Canvas.drawOBBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawOrbitoryBouncyBallView(scale, size, paint)
    restore()
}
