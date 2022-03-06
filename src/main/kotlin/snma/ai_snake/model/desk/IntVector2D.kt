package snma.ai_snake.model.desk

data class IntVector2D(val x: Int, val y: Int)

operator fun IntVector2D.plus(other: IntVector2D) = IntVector2D(x + other.x, y + other.y)
operator fun IntVector2D.times(int: Int) = IntVector2D(x * int, y * int)

fun IntVector2D.plusScaled(other: IntVector2D, scale: Int) = IntVector2D(x + other.x * scale, y + other.y * scale)

val IntVector2D.squareLength
    get() = x * x + y * y