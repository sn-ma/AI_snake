package snma.ai_snake.linear_algebra

import kotlin.random.Random

class Matrix(
    val rows: Int,
    val columns: Int,
    initializer: (Int, Int) -> Double = { _, _ -> 0.0 }
) {
    private val data = Array(columns * rows) { index ->
        initializer(xFromIndex(index), yFromIndex(index))
    }

    operator fun get(x: Int, y: Int) = data[indexFromXY(x, y)]
    operator fun set(x: Int, y: Int, value: Double) { data[indexFromXY(x, y)] = value }

    override fun toString() = buildString {
        append("Matrix<$rows, $columns>(\n")
        for (i in 0 until rows) {
            append("\t")
            for (j in 0 until columns) {
                append(this@Matrix[i, j])
                append(' ')
            }
            append("\n")
        }
        append(")")
    }

    private fun xFromIndex(index: Int) = index / columns
    private fun yFromIndex(index: Int) = index % columns
    private fun indexFromXY(x: Int, y: Int) = x * columns + y
}

fun Matrix.applyInPlace(function: (Double) -> Double) {
    for (i in 0 until rows) {
        for (j in 0 until columns) {
            this[i, j] = function(this[i, j])
        }
    }
}

fun Matrix.multiplyTo(multiplier: Matrix, destination: Matrix) {
    check(columns == multiplier.rows)
    check(rows == destination.rows)
    check(multiplier.columns == destination.columns)

    for (i in 0 until destination.rows) {
        for (j in 0 until destination.columns) {
            destination[i, j] = (0 until columns).sumOf { this[i, it] * multiplier[it, j] }
        }
    }
}

//fun main() {
//    val a = Matrix(3, 4) { _, _ -> Random.nextDouble(-5.0, 5.0) }
//    println(a)
//    val b = Matrix(4, 1) { _, _ -> Random.nextDouble(-5.0, 5.0) }
//    println(b)
//    val c = Matrix(3, 1)
//    println(c)
//    a.multiplyTo(b, c)
//    println(c)
//}

