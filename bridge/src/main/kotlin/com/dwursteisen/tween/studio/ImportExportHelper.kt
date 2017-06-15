package com.dwursteisen.tween.studio

import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenUtils
import java.util.*
import java.util.function.Consumer

object ImportExportHelper {
    fun stringToDummyTimeline(str: String): Timeline {
        val tl = Timeline.createParallel()
        val lines = str.split("\n".toRegex())

        val header = lines.takeWhile { it != "---" }
        val properties = lines.dropWhile { it != "---" }.drop(1)

        // lire propriete forme ici
        properties.map { line -> line.split(";".toRegex()) }
                .filter { parts -> parts.count() >= 7 }
                .map { parts ->
                    val targetName = parts[0]
                    var targetClass: Class<*>? = null
                    targetClass = Class.forName(parts[1])

                    val tweenType = Integer.parseInt(parts[2])
                    val delay = Integer.parseInt(parts[3])
                    val duration = Integer.parseInt(parts[4])
                    val equation = TweenUtils.parseEasing(parts[5])

                    val targets = FloatArray(parts.size - 6)
                    for (i in targets.indices)
                        targets[i] = java.lang.Float.parseFloat(parts[i + 6])


                    val userData: Tween = Tween.to(null, tweenType, duration.toFloat())
                            .cast(targetClass)
                            .target(*targets)
                            .ease(equation)
                            .delay(delay.toFloat())
                            .setUserData(targetName)
                    userData

                }.forEach(Consumer<Tween> { tl.push(it) })

        return tl
    }

    fun timelineToString(timeline: Timeline, targetsNamesMap: Map<Any, String>): String {
        val str = StringBuilder()

        // TODO: append propriete des objs ici
        timeline.children.map { it as Tween }
                .forEach({ tween ->
                    str.append(String.format(Locale.US, "%s;%s;%d;%d;%d;%s",
                            targetsNamesMap[tween.target],
                            tween.targetClass.name,
                            tween.type,
                            tween.delay.toInt(),
                            tween.duration.toInt(),
                            tween.easing.toString()))

                    for (i in 0..tween.combinedAttributesCount - 1)
                        str.append(String.format(Locale.US, ";%f", tween.targetValues[i]))

                    str.append("\n")
                })

        return str.toString()
    }
}