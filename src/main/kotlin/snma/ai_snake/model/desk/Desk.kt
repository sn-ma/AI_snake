package snma.ai_snake.model.desk

import java.util.*
import kotlin.random.Random

class Desk {
    val width: Int
        get() = DeskConstants.DESK_WIDTH

    val height: Int
        get() = DeskConstants.DESK_HEIGHT

    var applePos = randomPos()
        private set

    // Head pos is the first in the list
    private val _snakePositions = LinkedList<IntVector2D>().apply {
        val startPos = randomPos()
        repeat(DeskConstants.START_SNAKE_LENGTH) {
            addLast(startPos)
        }
    }

    val snakePositions: List<IntVector2D>
        get() = _snakePositions

    val snakeHeadPosition: IntVector2D
        get() = _snakePositions[0]

    var snakeEnergy = DeskConstants.ENERGY_INITIAL
        private set

    var isFinished = false
        private set

    var stepsCount = 0L
        private set

    val applesEaten: Int
        get() = snakePositions.size - DeskConstants.START_SNAKE_LENGTH

    fun isInDesk(position: IntVector2D) =
        position.x in 0 until width && position.y in 0 until height

    fun step(direction: MainDirection): StepResult {
        check(!isFinished)

        ++stepsCount

        if (--snakeEnergy <= 0) {
            isFinished = true
            return StepResult.DEATH
        }

        val newPos = snakeHeadPosition + direction.vector
        if (!isInDesk(newPos) || snakePositions.any { it == newPos }) {
            isFinished = true
            return StepResult.DEATH
        }
        _snakePositions.addFirst(newPos)
        return if (newPos == applePos) {
            snakeEnergy += DeskConstants.ENERGY_PER_APPLE
            do {
                applePos = randomPos()
            } while (applePos in snakePositions)
            StepResult.EAT_AN_APPLE
        } else {
            _snakePositions.removeLast()
            StepResult.JUST_A_STEP
        }
    }

    private fun randomPos() = IntVector2D(
        Random.nextInt(width),
        Random.nextInt(height),
    )
}