package cdsre

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

class ScriptViewController: Initializable {

	override fun initialize(p0: URL?, p1: ResourceBundle?) {
	}

	@FXML
	lateinit var script_view: AnchorPane

	@FXML
	lateinit var files: TabPane
}