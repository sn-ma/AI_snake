package snma.ai_snake.model.simulation

import org.slf4j.LoggerFactory
import snma.ai_snake.model.brain.AnnBrain
import snma.ai_snake.model.brain.Brain
import snma.ai_snake.model.brain.BrainMultiFactory
import snma.ai_snake.model.brain.ScoreFunction
import snma.ai_snake.model.desk.Desk
import kotlin.random.Random

class GenerationalSimulation<BRAIN: Brain>(
    val generationsCount: Int,
    val generationSize: Int,
    private val brainMultiFactory: BrainMultiFactory<BRAIN>,
    val scoreFunction: ScoreFunction,
) {
    var currentGenerationIndex = 0
        private set

    val isFinished: Boolean
        get() = currentGenerationIndex == generationsCount

    var currentSimulation = GroupSimulation(generationSize, brainMultiFactory::createFirstGeneration)
        private set

    enum class Result {
        NEXT_GENERATION_CREATED,
        NORMAL_STEP,
        FINISHED,
    }

    fun step(): Result {
        if (isFinished) {
            return Result.FINISHED
        }
        return if (currentSimulation.isFinished) {
            ++currentGenerationIndex
            if (isFinished) {
                return Result.FINISHED
            }
            val probabilitiesMap = ProbabilitiesMap<BRAIN>(currentSimulation, scoreFunction)
            currentSimulation = GroupSimulation(generationSize) { index, desk ->
                brainMultiFactory.createAnotherGeneration(index, desk, probabilitiesMap.selectRandom(), probabilitiesMap.selectRandom())
            }
            Result.NEXT_GENERATION_CREATED
        } else {
            currentSimulation.step()
            Result.NORMAL_STEP
        }
    }

    private class ProbabilitiesMap<BRAIN: Brain>(groupSimulation: GroupSimulation, scoreFunction: ScoreFunction) {
        private val scoresSum: Double
        private val brains: List<Pair<Double, BRAIN>>

        init {
            scoresSum = groupSimulation.desks.sumOf { scoreFunction(it) }
            brains = groupSimulation.desks.indices.map { index ->
                scoreFunction(groupSimulation.desks[index]) to groupSimulation.brains[index] as BRAIN
            }
        }

        fun selectRandom(): BRAIN {
            var l = Random.nextDouble(scoresSum)
            brains.forEach { (score, brain) ->
                if (l < score) {
                    return brain
                }
                l -= score
            }
            return brains.last().second
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GenerationalSimulation::class.java)
    }
}