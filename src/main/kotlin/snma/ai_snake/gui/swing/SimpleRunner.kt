package snma.ai_snake.gui.swing

import io.reactivex.rxjava3.core.Observable
import snma.ai_snake.model.brain.AnnBrain
import snma.ai_snake.model.brain.RoundWalkerBrain
import snma.ai_snake.model.brain.SingleDirectionWalker
import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.desk.Direction
import snma.ai_snake.model.desk.MainDirection
import snma.ai_snake.model.simulation.GroupSimulation
import java.awt.GridLayout
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main() {
    val columns = 10
    val rows = 6

    val simulation = GroupSimulation(columns * rows) { _, desk ->
//        SingleDirectionWalker(desk, MainDirection.DOWN)
//        RoundWalkerBrain(desk)
        AnnBrain.createRandom(desk)
    }
    val panels = simulation.desks.map { DeskPanel(it, 10) }

    JFrame("SnakeTest").apply {
        layout = GridLayout(rows, columns, 1, 1)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        panels.forEach { add(it) }
        isResizable = false
        pack()
        isVisible = true
    }

    val subscription = Observable.interval(500, TimeUnit.MILLISECONDS)
        .subscribe {
            if (!simulation.isFinished) {
                simulation.step()
                SwingUtilities.invokeLater { panels.forEach { deskPanel -> deskPanel.repaint() } }
            }
        }
}