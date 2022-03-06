package snma.ai_snake.model.brain

import org.slf4j.LoggerFactory
import snma.ai_snake.linear_algebra.Matrix
import snma.ai_snake.linear_algebra.applyInPlace
import snma.ai_snake.linear_algebra.multiplyTo
import snma.ai_snake.model.desk.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.asJavaRandom

class AnnBrain private constructor(
    desk: Desk,
    private val coefficients: List<Matrix>,
) : Brain(desk) {
    override fun think(): MainDirection {
        var data = InputBuilder.prepareInputData(desk)
        coefficients.forEachIndexed { index, matrix ->
            val newData = matrixCache.get().getOrCreate(matrix.rows, 1, data)
            matrix.multiplyTo(data, newData)
            data = newData
            if (index != coefficients.size - 1) {
                data.applyInPlace(::improvedRelu)
            }
        }
        val argmax = (0 until 4)
            .asSequence()
            .map { it to data[it, 0] }
            .maxByOrNull { it.second }!!
            .first

        return MainDirection.values()[argmax]
    }

    private fun relu(value: Double) = max(value, 0.0)
    private fun improvedRelu(value: Double) = max(value, value * 0.1)

    companion object {
        private val logger = LoggerFactory.getLogger(AnnBrain::class.java)

        private val matrixCache = ThreadLocal.withInitial { MatrixBufferCache() }

        private val dimensionsPairs: List<Pair<Int, Int>>

        init {
            val dimensions = sequenceOf(3 * 8) +
                    BrainConstants.HIDDEN_NEURONS_COUNTS.asSequence() +
                    sequenceOf(4)
            dimensionsPairs = dimensions.zipWithNext().toList()
        }

        fun createRandom(desk: Desk): AnnBrain {
            val coefficients = dimensionsPairs.map { (prevDimension, nextDimension) ->
                Matrix(nextDimension, prevDimension) { _, _ ->
                    var value: Double
                    do {
                        value = Random.nextDouble(-1.0, 1.0)
                    } while (value.absoluteValue < 0.1)
                    value
                }
            }

            return AnnBrain(
                desk = desk,
                coefficients = coefficients,
            )
        }

        fun createFromParents(desk: Desk, parent1: AnnBrain, parent2: AnnBrain): AnnBrain {
            val coefficients = dimensionsPairs.mapIndexed { index, (prevDimension, nextDimension) ->
                Matrix(nextDimension, prevDimension) { i, j ->
                    val mutation = if (Random.nextDouble() < BrainConstants.MUTATION_PROBABILITY) {
                        Random.asJavaRandom().nextGaussian()
                    } else {
                        0.0
                    }
                    if (Random.nextBoolean()) {
                        parent1
                    } else {
                        parent2
                    }.coefficients[index][i, j] + mutation
                }
            }

            return AnnBrain(
                desk = desk,
                coefficients = coefficients,
            )
        }

        private object InputBuilder {
            fun prepareInputData(desk: Desk): Matrix {
                val matrix = matrixCache.get().getOrCreate(3 * 8, 1)
                for (row in 0 until matrix.rows) {
                    val direction = ALL_DIRECTIONS[row / 3]
                    matrix[row, 0] = when (row % 3) {
                        0 -> distanceToBorderScaled(desk, direction)
                        1 -> distanceToTailScaled(desk, direction)
                        2 -> distanceToAppleScaled(desk, direction)
                        else -> error("This could never happen")
                    }
                }
                return matrix
            }

            private fun allPositionsInDirection(startPosition: IntVector2D, step: IntVector2D) = sequence {
                var i = 1
                while (true) {
                    yield(startPosition.plusScaled(step, i))
                    ++i
                }
            }

            private fun distanceToBorderScaled(desk: Desk, direction: Direction): Double {
                val distance = allPositionsInDirection(desk.snakeHeadPosition, direction.vector)
                    .indexOfFirst { !desk.isInDesk(it) } + 1
                return 1.0 / distance
            }

            private fun distanceToAppleScaled(desk: Desk, direction: Direction): Double {
                val distance = allPositionsInDirection(desk.snakeHeadPosition, direction.vector)
                    .takeWhile { desk.isInDesk(it) }
                    .indexOfFirst { it == desk.applePos }
                    .takeIf { it != -1 }
                    ?.let { it + 1 }
                return distance?.let { 1.0 / it } ?: 0.0
            }

            private fun distanceToTailScaled(desk: Desk, direction: Direction): Double {
                val distance = allPositionsInDirection(desk.snakeHeadPosition, direction.vector)
                    .takeWhile { desk.isInDesk(it) }
                    .indexOfFirst { it in desk.snakePositions }
                    .takeIf { it != -1 }
                    ?.let { it + 1 }
                return distance?.let { 1.0 / it } ?: 0.0
            }
        }
    }
}

private class MatrixBufferCache {
    private val cache = mutableMapOf<Pair<Int, Int>, MutableList<Matrix>>()

    fun getOrCreate(rows: Int, columns: Int, vararg notThese: Matrix): Matrix {
        val list = cache.getOrPut(rows to columns, ::mutableListOf)
        return list.firstOrNull { it !in notThese }
            ?: Matrix(rows, columns)
                .also { list.add(it) }
    }
}