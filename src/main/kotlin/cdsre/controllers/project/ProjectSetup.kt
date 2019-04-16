package cdsre.controllers.project

import javafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane




class ProjectSetup {

    @FXML
    lateinit var projectsetup: AnchorPane

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
        //TODO: Implement
    }
}