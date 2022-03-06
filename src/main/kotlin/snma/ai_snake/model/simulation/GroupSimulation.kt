package snma.ai_snake.model.simulation

import snma.ai_snake.model.brain.Brain
import snma.ai_snake.model.desk.Desk

class GroupSimulation(
    val count: Int,
    brainFactory: (Int, Desk) -> Brain
) {
    private val indices = (0 until count).toList()
    val desks = (1 .. count).map { Desk() }
    val brains = desks
        .mapIndexed(brainFactory)

    var simulationsRunning = count
        private set

    var currentStep = 0L
        private set

    val isFinished: Boolean
        get() = simulationsRunning == 0

    init {
        check(count > 0)
    }

    fun step() {
        if (isFinished) {
            return
        }

        ++currentStep

        indices.stream().forEach { index ->
            val desk = desks[index]
            val brain = brains[index]
            if (!desk.isFinished) {
                desk.step(brain.think())
            }
        }

        simulationsRunning = desks.count { !it.isFinished }
    }
}