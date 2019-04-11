package cdsre.controllers.details

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

class MapHeaderDetailController: Initializable {

	@FXML
	lateinit var mapheaders_details: AnchorPane

	@FXML
	lateinit var buildingpackage: Spinner<*>

	@FXML
	lateinit var maintileset: Spinner<*>

	override fun initialize(p0: URL?, p1: ResourceBundle?) {
		buildingpackage.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		maintileset.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
	}
}