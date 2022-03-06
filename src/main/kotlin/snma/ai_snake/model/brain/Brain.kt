package snma.ai_snake.model.brain

import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.desk.MainDirection

abstract class Brain(protected val desk: Desk) {
    abstract fun think(): MainDirection
}

interface BrainMultiFactory<BRAIN: Brain> {
    fun createFirstGeneration(index: Int, desk: Desk): BRAIN
    fun createAnotherGeneration(index: Int, desk: Desk, parent1: BRAIN, parent2: BRAIN): BRAIN
}