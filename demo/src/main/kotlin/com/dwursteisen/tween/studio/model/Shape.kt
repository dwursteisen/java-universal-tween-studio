package com.dwursteisen.tween.studio.model

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

data class Shape(var name: String,
                 var color: Color,
                 var position: Vector2 = Vector2(),
                 var size: Vector2 = Vector2(),
                 var scale: Vector2 = Vector2(1f, 1f),
                 var rotation: Float = 0f)