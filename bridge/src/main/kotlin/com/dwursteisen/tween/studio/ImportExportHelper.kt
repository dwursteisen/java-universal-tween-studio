package com.dwursteisen.tween.studio

import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenUtils
import java.util.*
import java.util.function.Consumer

object ImportExportHelper {
    fun stringToDummyTimeline(str: String): Import {
        val tl = Timeline.createParallel()
        val lines = str.split("\n".toRegex())

        val properties = lines.takeWhile { it != "---" }
        val header = lines.dropWhile { it != "---" }.drop(1)

        val confs = header.map {
            it.split(";".toRegex())
        }.filter { parts -> parts.count() >= 3 }
                .map {
                    TargetConfiguration(name = it[0],
                            color = it[1],
                            size = Pair(it[2].toInt(), it[3].toInt()),
                            position = Pair(it[4].toInt(), it[5].toInt()))
                }

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

        return Import(confs = confs, timeline = tl)
    }

    fun timelineToString(confs: List<TargetConfiguration>, timeline: Timeline, targetsNamesMap: Map<Any, String>): String {
        val str = StringBuilder()



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
        str.append("---")

        confs.map {
            "${it.name};${it.color};${it.size.first};${it.size.second};${it.position.first};${it.size.second}\n"
        }.forEach({ str.append(it) })


        return str.toString()
    }
}