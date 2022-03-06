package snma.ai_snake.model.brain

import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.desk.MainDirection

class SingleDirectionWalker(
    desk: Desk,
    private val direction: MainDirection
) : Brain(desk) {
    override fun think() = direction
}