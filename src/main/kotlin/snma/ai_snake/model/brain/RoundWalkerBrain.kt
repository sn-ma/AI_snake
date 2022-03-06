package snma.ai_snake.model.brain

import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.desk.MainDirection

// Just for testing
class RoundWalkerBrain(desk: Desk): Brain(desk) {
    private var state = -1

    override fun think(): MainDirection {
        state = (state + 1) % route.size
        return route[state]
    }

    companion object {
        private val route = listOf(
            MainDirection.UP,
            MainDirection.UP,
            MainDirection.UP,
            MainDirection.UP,
            MainDirection.RIGHT,
            MainDirection.RIGHT,
            MainDirection.RIGHT,
            MainDirection.RIGHT,
            MainDirection.DOWN,
            MainDirection.DOWN,
            MainDirection.DOWN,
            MainDirection.DOWN,
            MainDirection.LEFT,
            MainDirection.LEFT,
            MainDirection.LEFT,
            MainDirection.LEFT,
        )
    }
}