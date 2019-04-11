package cdsre.controllers.master

import javafx.beans.value.ObservableValue
import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList

class PokemonMasterController: Initializable {

	override fun initialize(p0: URL?, p1: ResourceBundle?) {

		val filteredList = FilteredList(masterData) { data -> true }
		pokemonlistings.items = filteredList

		search.textProperty().addListener { observableValue: ObservableValue<out String>, oldVal: String, newVal: String ->
			filteredList.setPredicate{
			if (newVal == null || newVal.isEmpty()){
				true
			}
			var lowerCaseSearch = newVal.toLowerCase()

			it.toLowerCase().contains(lowerCaseSearch)
		}
		}

		for (i in 1..492) {
			masterData.add("Item $i")
		}
	}

	@FXML
	lateinit var pokemon_master: AnchorPane

	@FXML
	lateinit var search: TextField

	@FXML
	lateinit var pokemonlistings: ListView<*>

	var masterData: ObservableList<String> = FXCollections.observableArrayList<String>()
}