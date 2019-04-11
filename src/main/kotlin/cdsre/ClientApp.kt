package cdsre

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class ClientApp: Application() {

	override fun start(stage: Stage?) {

		globalStage = stage!!

		val root = FXMLLoader.load<Parent>(this.javaClass.classLoader.getResource("cdsre.fxml"))

		stage.scene = Scene(root)

		stage.scene.stylesheets.add(this.javaClass.classLoader.getResource("css/main.css").toExternalForm())

		stage.title = "CDSRE v1.0"
		stage.show()
	}

	companion object {
		@JvmStatic
		lateinit var globalStage: Stage
	}
}