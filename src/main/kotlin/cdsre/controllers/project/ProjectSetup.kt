package cdsre.controllers.project

import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import java.net.URL
import java.util.*
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.stage.DirectoryChooser
import java.util.concurrent.Callable


class ProjectSetup: Initializable {

    @FXML
    lateinit var projectsetup: AnchorPane

    @FXML
    lateinit var baserom_dest: TextField

    @FXML
    lateinit var project_dest: TextField

    @FXML
    lateinit var mappings: ChoiceBox<*>

    @FXML
    lateinit var baserom_select: Button

    @FXML
    lateinit var project_select: Button

    @FXML
    lateinit var create: Button

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

        val field1NotEmpty = createBooleanBinding(Callable<Boolean> {
            baserom_dest.text.isNotEmpty()
        }, baserom_dest.textProperty())

        val field2NotEmpty = createBooleanBinding(Callable<Boolean> {
            project_dest.text.isNotEmpty()
        }, project_dest.textProperty())

        create.disableProperty().bind(field1NotEmpty.not().or(field2NotEmpty.not()))
    }

    fun start()
    {
        val root: Parent = FXMLLoader.load(javaClass.classLoader.getResource("graphics/project/projectsetup.fxml"))

        var stage = Stage()
        stage.title = "Project Setup"
        stage.scene = Scene(root)

        stage.scene.stylesheets.add(this.javaClass.classLoader.getResource("css/main.css").toExternalForm())

        stage.show()
    }

    @FXML
    fun close(event: MouseEvent) {
        projectsetup.scene.window.hide()
    }

    @FXML
    fun createProject(event: MouseEvent) {
        projectsetup.scene.window.hide()
    }

    @FXML
    fun selectBaseROM(event: ActionEvent) {
        var fileChooser = FileChooser()
        fileChooser.title = "Open File"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Nintendo DS ROM", "*.nds")
        )

        var file = fileChooser.showOpenDialog(projectsetup.scene.window)

        baserom_dest.text = file.path
    }

    @FXML
    fun selectProjectDest(event: ActionEvent) {
        var directoryChooser = DirectoryChooser()
        directoryChooser.title = "Select Directory"

        var file = directoryChooser.showDialog(projectsetup.scene.window)

        project_dest.text = file.path
    }
}