package cdsre.controllers.prefs

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import java.net.URL
import java.util.*
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import javafx.stage.DirectoryChooser
import java.util.concurrent.Callable
import javafx.scene.control.TreeItem




class Preferences: Initializable {

    @FXML
    lateinit var preferences: AnchorPane

    @FXML
    lateinit var tree: TreeView<String>

    @FXML
    lateinit var content: AnchorPane

    @FXML
    lateinit var cancel: Button

    @FXML
    lateinit var apply: Button

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

        tree.root = TreeItem("Preferences")

        val generalItem = TreeItem("General")
        val mappingsItem = TreeItem("Mappings")
        generalItem.children.add(mappingsItem)
        mappingsItem.children.add(TreeItem("Local Mappings"))

        val appearanceItem = TreeItem("Appearance")
        appearanceItem.children.add(TreeItem("Themes"))

        tree.root.children.add(generalItem)
        tree.root.children.add(appearanceItem)

        tree.isShowRoot = false

        tree.selectionModel.selectedItemProperty().addListener { observable, prevSelectedItem, selectedItem ->

           println(selectedItem.value)
        }
    }

    fun start()
    {
        val root: Parent = FXMLLoader.load(javaClass.classLoader.getResource("graphics/prefs/prefs.fxml"))

        var stage = Stage()
        stage.title = "Preferences"
        stage.scene = Scene(root)

        stage.scene.stylesheets.add(this.javaClass.classLoader.getResource("css/main.css").toExternalForm())

        stage.show()
    }

    @FXML
    fun close(event: MouseEvent) {
        preferences.scene.window.hide()
    }

    @FXML
    fun applyChanges(event: MouseEvent) {
        preferences.scene.window.hide()
    }
}