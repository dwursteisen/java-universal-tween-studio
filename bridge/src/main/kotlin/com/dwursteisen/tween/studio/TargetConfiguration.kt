package com.dwursteisen.tween.studio

import aurelienribon.tweenengine.Timeline

typealias Color = String

data class TargetConfiguration(val name: String = "",
                               val size: Pair<Int, Int> = Pair(0, 0),
                               val position: Pair<Int, Int> = Pair(0, 0),
                               val color: Color = "")


data class Import(val confs: List<TargetConfiguration>, val timeline: Timeline)
data class Export(val confs: List<TargetConfiguration>, val timeline: Timeline)