package cdsre.controllers.details

import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.scene.control.ChoiceBox
import javafx.fxml.FXML
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.AnchorPane



class PokemonDetailController: Initializable {

	@FXML
	lateinit var pokemon_details: AnchorPane

	@FXML
	lateinit var hp: Spinner<*>

	@FXML
	lateinit var attack: Spinner<*>

	@FXML
	lateinit var defense: Spinner<*>

	@FXML
	lateinit var speed: Spinner<*>

	@FXML
	lateinit var specialattack: Spinner<*>

	@FXML
	lateinit var specialdefense: Spinner<*>

	@FXML
	lateinit var baseexperience: Spinner<*>

	@FXML
	lateinit var basehappiness: Spinner<*>

	@FXML
	lateinit var catchrate: Spinner<*>

	@FXML
	lateinit var marshchance: Spinner<*>

	@FXML
	lateinit var genderratio: ChoiceBox<*>

	@FXML
	lateinit var maxexperience: ChoiceBox<*>

	@FXML
	lateinit var firsttype: ChoiceBox<*>

	@FXML
	lateinit var secondtype: ChoiceBox<*>

	@FXML
	lateinit var firsteggtype: ChoiceBox<*>

	@FXML
	lateinit var secondeggtype: ChoiceBox<*>

	@FXML
	lateinit var stepstohatch: Spinner<*>

	@FXML
	lateinit var firstitem: ChoiceBox<*>

	@FXML
	lateinit var seconditem: ChoiceBox<*>

	@FXML
	lateinit var firstability: ChoiceBox<*>

	@FXML
	lateinit var secondability: ChoiceBox<*>

	override fun initialize(p0: URL?, p1: ResourceBundle?) {
		hp.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		attack.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		defense.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		speed.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		specialattack.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		specialdefense.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)

		baseexperience.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		basehappiness.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255)

		catchrate.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		marshchance.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)

		stepstohatch.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
	}
}