package cdsre

import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory


class FileController: Initializable {

    lateinit var script_view: AnchorPane
    lateinit var file: Tab

    @FXML
    lateinit var container: AnchorPane

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

    }

    fun establishFile()
    {
        file.content = container

        // Create text area for scripting
        var script_area = CodeArea()
        script_area.style = ".code-area"

        script_area.paragraphGraphicFactory = LineNumberFactory.get(script_area)

        //TODO: These should be bound to the tab.
        script_area.prefWidthProperty().bind(container.widthProperty())
        script_area.prefHeightProperty().bind(container.heightProperty())

        script_area.isWrapText = true

        println(container.width)
        println(container.height)
        container.children.add(script_area)
    }
}