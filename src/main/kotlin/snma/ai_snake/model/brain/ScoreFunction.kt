package snma.ai_snake.model.brain

import snma.ai_snake.model.desk.Desk
import kotlin.math.pow

fun interface ScoreFunction {
    operator fun invoke(desk: Desk): Double
}

class ScoreFunction1: ScoreFunction {
    override fun invoke(desk: Desk): Double {
        return desk.stepsCount.toDouble()
    }
}

class ScoreFunction2: ScoreFunction {
    override fun invoke(desk: Desk): Double {
        return desk.stepsCount.toDouble().pow(3)
    }
}

class ScoreFunction3: ScoreFunction {
    override fun invoke(desk: Desk): Double {
        return desk.stepsCount.toDouble().pow(3) * 2.0.pow(desk.applesEaten)
    }
}