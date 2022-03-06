package snma.ai_snake.gui.swing

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import net.miginfocom.swing.MigLayout
import snma.ai_snake.model.brain.*
import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.simulation.GenerationalSimulation
import snma.ai_snake.plot_data_prepare.PlotDataExporter
import java.awt.GridLayout
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val updateIntervals = listOf(1, 2, 3, 4, 6, 8, 16, 32, 64, 128, 256)
    val updateIntervalIndex = AtomicInteger(updateIntervals.size - 1)

    val columns = 30
    val rows = 18

    val simulation = GenerationalSimulation(
        generationsCount = 100_000,
        generationSize = 500,
        scoreFunction = ScoreFunction3(),
        brainMultiFactory = object : BrainMultiFactory<AnnBrain> {
            override fun createFirstGeneration(index: Int, desk: Desk) = AnnBrain.createRandom(desk)
            override fun createAnotherGeneration(
                index: Int,
                desk: Desk,
                parent1: AnnBrain,
                parent2: AnnBrain
            ) = AnnBrain.createFromParents(desk, parent1, parent2)
        }
//        brainMultiFactory = object : BrainMultiFactory<RoundWalkerBrain> {
//            override fun createFirstGeneration(index: Int, desk: Desk) = RoundWalkerBrain(desk)
//            override fun createAnotherGeneration(
//                index: Int,
//                desk: Desk,
//                parent1: RoundWalkerBrain,
//                parent2: RoundWalkerBrain
//            ) = RoundWalkerBrain(desk)
//        }
    )
    val panels = simulation.currentSimulation.desks
        .asSequence()
        .take(columns * rows)
        .map { DeskPanel(it, 3) }
        .toList()

    val label = JLabel("Initializing")

    JFrame("AI Snake experiment").apply {
        layout = MigLayout("wrap 2")
        add(label, "spanx 2")
        add(JButton("Slow down").apply { addActionListener {
            updateIntervalIndex.updateAndGet { min(updateIntervals.size - 1, it + 1) }
        } })
        add(JButton("Speed up").apply { addActionListener {
            updateIntervalIndex.updateAndGet { max(0, it - 1) }
        } })
        add(JPanel(GridLayout(rows, columns, 1, 1)).apply {
            panels.forEach { add(it) }
        }, "spanx 2, grow")
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        isResizable = false
        pack()
        isVisible = true
    }

    val plotDataExporter = PlotDataExporter("./plot_data.tsv")

    Flowable.interval(4, TimeUnit.MILLISECONDS)
        .filter { it % updateIntervals[updateIntervalIndex.get()] == 0L }
        .onBackpressureLatest()
        .subscribe {
            SwingUtilities.invokeAndWait {
                if (!simulation.isFinished) {
                    val stepResult = simulation.step()
                    if (stepResult == GenerationalSimulation.Result.NEXT_GENERATION_CREATED) {
                        simulation.currentSimulation.desks
                            .asSequence()
                            .zip(panels.asSequence())
                            .forEach { (desk, panel) -> panel.desk = desk }
                    }
                    if (stepResult == GenerationalSimulation.Result.NORMAL_STEP && simulation.currentSimulation.isFinished) {
                        plotDataExporter.storeGenerationInfo(simulation)
                    }
                    val text = buildString {
                        append("Generation ${simulation.currentGenerationIndex}, ")
                        append("step ${simulation.currentSimulation.currentStep}. ")
                        append("${simulation.currentSimulation.simulationsRunning}/${simulation.generationSize} alive. ")
                        append("Speed #${updateIntervalIndex.get() + 1} / ${updateIntervals.size}.")
                    }
                    label.text = text
                    panels.forEach { deskPanel -> deskPanel.repaint() }
                }
            }
        }
}