package com.dwursteisen.tween.studio.model

import aurelienribon.tweenengine.TweenAccessor

class ShapeAccessor : TweenAccessor<Shape> {

    companion object {
        val POSITION_XY = 1
        val SCALE_XY = 2
        val ROTATION = 3
        val OPACITY = 4
    }

    override fun getValues(target: Shape, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            POSITION_XY -> {
                returnValues[0] = target.position.x
                returnValues[1] = target.position.y
                return 2
            }

            SCALE_XY -> {
                returnValues[0] = target.scale.x
                returnValues[1] = target.scale.y
                return 2
            }

            ROTATION -> {
                returnValues[0] = target.rotation
                return 1
            }
            OPACITY -> {
                returnValues[0] = target.color.a
                return 1
            }
            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Shape, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            POSITION_XY -> target.position.set(newValues[0], newValues[1])
            SCALE_XY -> target.scale.set(newValues[0], newValues[1])
            ROTATION -> target.rotation = newValues[0]
            OPACITY -> {
                val c = target.color
                c.set(c.r, c.g, c.b, newValues[0])
            }

            else -> assert(false)
        }

    }

}