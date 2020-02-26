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

class OrbitoryBouncyBallView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class OBBNode(var i : Int, val state : State = State()) {

        private var next : OBBNode? = null
        private var prev : OBBNode? = null

        init {

        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = OBBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawOBBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : OBBNode {
            var curr : OBBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class OrbitoryBouncyBall(var i : Int) {

        private val root : OBBNode = OBBNode(0)
        private var curr = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : OrbitoryBouncyBallView) {

        private val animator : Animator = Animator(view)
        private val obb : OrbitoryBouncyBall = OrbitoryBouncyBall(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            obb.draw(canvas, paint)
            animator.animate {
                obb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            obb.startUpdating {
                animator.start()
            }
        }
    }
}