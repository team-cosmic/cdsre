package cdsre.controllers.details

import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ComboBox


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
	lateinit var firsttype: ComboBox<Image>

	@FXML
	lateinit var secondtype: ComboBox<Image>

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

    @FXML
    lateinit var battledata: TitledPane

    @FXML
    lateinit var otherdata: TitledPane

    @FXML
    lateinit var types: TitledPane

    @FXML
    lateinit var breeding: TitledPane

    @FXML
    lateinit var holditems: TitledPane

    @FXML
    lateinit var abilities: TitledPane

    @FXML
    lateinit var collapseall: Button

    @FXML
    lateinit var expandall: Button

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

		val images = fetchImages()
		firsttype.items.addAll(images)
		firsttype.buttonCell = ImageListCell()
		firsttype.setCellFactory { listView -> ImageListCell() }
		firsttype.selectionModel.select(0)

		secondtype.items.addAll(images)
		secondtype.buttonCell = ImageListCell()
		secondtype.setCellFactory { listView -> ImageListCell() }
		secondtype.selectionModel.select(0)
	}

	private fun fetchImages():ObservableList<Image> {
		val data = FXCollections.observableArrayList<Image>()

		//TODO: maybe loop through them all instead?
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/unknown.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/bug.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/dark.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/dragon.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/electric.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/fight.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/fire.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/flying.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/ghost.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/grass.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/ground.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/ice.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/normal.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/poison.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/psychic.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/rock.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/steel.png")))
		data.add(Image(this.javaClass.getResourceAsStream("/graphics/types/water.png")))
		return data
	}

    @FXML
    fun expandAll()
    {
        battledata.isExpanded = true
        otherdata.isExpanded = true
        types.isExpanded = true
        breeding.isExpanded = true
        holditems.isExpanded = true
        abilities.isExpanded = true
    }

    @FXML
    fun collapseAll()
    {
        battledata.isExpanded = false
        otherdata.isExpanded = false
        types.isExpanded = false
        breeding.isExpanded = false
        holditems.isExpanded = false
        abilities.isExpanded = false
    }

}

internal class ImageListCell : ListCell<Image>() {
	private val view: ImageView

	init {
		contentDisplay = ContentDisplay.GRAPHIC_ONLY
		view = ImageView()
	}

	override fun updateItem(item: Image?, empty: Boolean) {
		super.updateItem(item, empty)

		if (item == null || empty) {
			graphic = null
		} else {
			view.image = item
			graphic = view
		}
	}

}