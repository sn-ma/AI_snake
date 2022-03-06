package snma.ai_snake.model.desk

sealed interface Direction {
    val vector: IntVector2D
}

enum class MainDirection(override val vector: IntVector2D) : Direction {
    UP(IntVector2D(0, 1)),
    DOWN(IntVector2D(0, -1)),
    LEFT(IntVector2D(-1,0)),
    RIGHT(IntVector2D(1,0)),
}

enum class DiagonalDirection(override val vector: IntVector2D) : Direction {
    UP_LEFT(IntVector2D(-1,1)),
    UP_RIGHT(IntVector2D(1,1)),
    DOWN_LEFT(IntVector2D(-1,-1)),
    DOWN_RIGHT(IntVector2D(1,-1)),
}

val ALL_DIRECTIONS: List<Direction> = MainDirection.values().asList() + DiagonalDirection.values().asList()