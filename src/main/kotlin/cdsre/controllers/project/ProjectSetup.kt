package cdsre.controllers.project

import javafx.scene.Scene
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage


class ProjectSetup {

    fun start()
    {
        val root: Parent = FXMLLoader.load(javaClass.classLoader.getResource("graphics/project/projectsetup.fxml"))

        val stage = Stage()

        stage.title = "Project Setup"
        stage.scene = Scene(root, 400.0, 600.0)

        stage.scene.stylesheets.add(this.javaClass.classLoader.getResource("css/main.css").toExternalForm())

        stage.show()
    }
}