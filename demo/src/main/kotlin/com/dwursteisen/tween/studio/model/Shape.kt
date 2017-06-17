package com.dwursteisen.tween.studio.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.dwursteisen.tween.studio.TargetConfiguration

fun String.toColor(): Color {
    // TODO: convert HEX to color
    return Color.RED
}

class Shape(var name: String,
            var color: Color,
            var position: Vector2 = Vector2(),
            var size: Vector2 = Vector2(),
            var scale: Vector2 = Vector2(1f, 1f),
            var rotation: Float = 0f) {
    companion object {
        fun fromConf(conf: TargetConfiguration): Shape {
            return Shape(
                    name = conf.name,
                    color = conf.color.toColor(),
                    position = Vector2(conf.position.first.toFloat(), conf.position.second.toFloat()),
                    size = Vector2(conf.size.first.toFloat(), conf.size.second.toFloat())
            )
        }
    }
}