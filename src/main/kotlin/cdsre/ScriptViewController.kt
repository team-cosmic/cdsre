package cdsre

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.AnchorPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.net.URL
import java.util.*

class ScriptViewController: Initializable {

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

        // Create text area for scripting
        var script_area = CodeArea()

        script_area.paragraphGraphicFactory = LineNumberFactory.get(script_area)

        script_area.prefWidthProperty().bind(script_view.widthProperty())
        script_area.prefHeightProperty().bind(script_view.heightProperty())

        script_area.isWrapText = true

        script_view.children.add(script_area)
    }

    @FXML
    lateinit var script_view: AnchorPane

}