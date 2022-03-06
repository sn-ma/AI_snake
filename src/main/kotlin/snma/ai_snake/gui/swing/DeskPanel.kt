package snma.ai_snake.gui.swing

import snma.ai_snake.model.desk.Desk
import snma.ai_snake.model.desk.IntVector2D
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class DeskPanel(
    var desk: Desk,
    private val pixelsPerCell: Int,
): JPanel() {
    init {
        check(pixelsPerCell in GuiConstants.PIXELS_PER_CELL_RANGE)
    }

    private val preferredWidth
        get() = pixelsPerCell * desk.width
    private val preferredHeight
        get() = pixelsPerCell * desk.height

    override fun getPreferredSize() = Dimension(preferredWidth, preferredHeight)

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g as Graphics2D

        g.color = GuiConstants.COLOR_BG
        g.fillRect(0, 0, preferredWidth, preferredHeight)

        fun fillCell(pos: IntVector2D) {
            val x = pos.x * pixelsPerCell
            val y = preferredHeight - (pos.y + 1) * pixelsPerCell

            g.fillRect(x, y, pixelsPerCell, pixelsPerCell)
        }

        g.color = GuiConstants.COLOR_APPLE
        fillCell(desk.applePos)

        g.color = GuiConstants.COLOR_SNAKE
        desk.snakePositions.forEach { fillCell(it) }

        if (desk.isFinished) {
            g.color = GuiConstants.COLOR_TEXT
            g.drawString("Game Over", 0, preferredHeight)
        }
    }
}