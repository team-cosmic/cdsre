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

	@FXML
	lateinit var label_tmdata: Label

	@FXML
	lateinit var label_movesets: Label

	@FXML
	lateinit var label_hmdata: Label

	@FXML
	lateinit var label_evolutionmethod: Label

	@FXML
	lateinit var label_evolution: Label

	@FXML
	lateinit var label_evolutiontree: Label

	@FXML
	lateinit var label_evolutionlevel: Label

	@FXML
	lateinit var label_happinessrequired: Label

	@FXML
	lateinit var label_evolutionitem: Label

	@FXML
	lateinit var label_evolutionpokemon: Label

	@FXML
	lateinit var label_evolutionattack: Label

	@FXML
	lateinit var label_evolvesinto: Label


	override fun initialize(p0: URL?, p1: ResourceBundle?) {
		learnlevel.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		evolutionlevel.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)
		happinessrequired.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE)

		label_tmdata.tooltip = Tooltip("Shows all the possible TMs the Pokemon is able to be taught.")
		label_movesets.tooltip = Tooltip("Shows the moves the Pokemon can learn as they level up.")
		label_hmdata.tooltip = Tooltip("Shows all the possible HMs the Pokemon is able to be taught.")

		label_evolutionmethod.tooltip = Tooltip("Sets the method by which the Pokemon can evolve.")
		label_evolution.tooltip = Tooltip("Shows the different possible evolutions a Pokemon can have.")
		label_evolutiontree.tooltip = Tooltip("Shows the evolution tree of a Pokemon relative to the currently selected Pokemon.")

		label_evolutionlevel.tooltip = Tooltip("Sets the level the Pokemon should evolve.")
		label_happinessrequired.tooltip = Tooltip("Sets the happiness the Pokemon will require to evolve. Ranges from 0-255.")
		label_evolutionitem.tooltip = Tooltip("Sets the item the Pokemon requires to hold in order to evolve.")
		label_evolutionpokemon.tooltip = Tooltip("Sets the required Pokemon the player must have in their party in order for this Pokemon to evolve.")
		label_evolutionattack.tooltip = Tooltip("Sets the required move the Pokemon must have in order to evolve.")
		label_evolvesinto.tooltip = Tooltip("The Pokemon this Pokemon will evolve into if all conditions are met.")
	}

}