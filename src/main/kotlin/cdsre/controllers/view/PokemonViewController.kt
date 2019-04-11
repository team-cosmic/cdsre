package cdsre.controllers.view

import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane



class PokemonViewController: Initializable {

	@FXML
	lateinit var pokemon_view: AnchorPane

	@FXML
	lateinit var selectalltms: Button

	@FXML
	lateinit var deselectalltms: Button

	@FXML
	lateinit var learnlevel: Spinner<*>

	@FXML
	lateinit var move: ChoiceBox<*>

	@FXML
	lateinit var addmove: Button

	@FXML
	lateinit var deletemove: Button

	@FXML
	lateinit var selectallhms: Button

	@FXML
	lateinit var deselectallhms: Button

	@FXML
	lateinit var evolutionmethod: ChoiceBox<*>

	@FXML
	lateinit var evolutionnumber: Slider

	@FXML
	lateinit var evolutionlevel: Spinner<*>

	@FXML
	lateinit var happinessrequired: Spinner<*>

	@FXML
	lateinit var evolutionitem: ChoiceBox<*>

	@FXML
	lateinit var evolutionpokemon: ChoiceBox<*>

	@FXML
	lateinit var evolutionattack: ChoiceBox<*>

	@FXML
	lateinit var evolvesinto: ChoiceBox<*>

	override fun initialize(p0: URL?, p1: ResourceBundle?) {
		learnlevel.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		evolutionlevel.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		happinessrequired.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
	}

}