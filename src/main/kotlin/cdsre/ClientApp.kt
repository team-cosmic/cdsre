package cdsre

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.Parent


class ClientApp : Application() {

    override fun start(stage: Stage?) {

        globalStage = stage!!

        val root = FXMLLoader.load<Parent>(this.javaClass.classLoader.getResource("cdsre.fxml"))

        stage.scene = Scene(root)
        stage.title = "CDSRE v1.0"
        stage.show()
    }

    companion object {
        @JvmStatic
        lateinit var globalStage: Stage
    }
}