package snma.ai_snake.plot_data_prepare

import snma.ai_snake.model.simulation.GenerationalSimulation
import snma.ai_snake.model.simulation.GroupSimulation
import java.io.File

class PlotDataExporter(filename: String) {
    private val file = File(filename).also { file ->
        file.writeText(buildString {
            append(Column.values().joinToString("\t") { it.shortName })
            append('\n')
        })
    }

    // This should be called on simulation generation finish, before start of a new one
    fun storeGenerationInfo(generationalSimulation: GenerationalSimulation<*>) {
        file.appendText(buildString {
            append(Column.values().joinToString("\t") { it.extractor(generationalSimulation)?.toString() ?: "null" })
            append('\n')
        })
    }

    enum class Column(val shortName: String, val extractor: GenerationalSimulation<*>.() -> Any?) {
        GENERATION_INDEX("generation_idx", { currentGenerationIndex }),
        MAX_STEPS("max_steps", { currentSimulation.currentStep }),
        MEAN_STEPS("mean_steps", { currentSimulation.desks
            .asSequence().map { desk -> desk.stepsCount }.average() }),
        MAX_EATEN_APPLES("max_apples", { currentSimulation.desks.maxOf { desk -> desk.applesEaten } }),
        MEAN_EATEN_APPLES("mean_apples", { currentSimulation.desks
            .asSequence().map { desk -> desk.applesEaten }.average() }),
        MAX_SCORE("max_score", { currentSimulation.desks.maxOf { scoreFunction(it) } }),
        MEAN_SCORE("mean_score", { currentSimulation.desks
            .asSequence().map { scoreFunction(it) }.average() }),
        ;
    }
}