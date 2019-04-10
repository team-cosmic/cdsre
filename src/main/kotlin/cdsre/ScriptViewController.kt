package cdsre

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

class ScriptViewController: Initializable {

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        addFile()
    }

    @FXML
    lateinit var script_view: AnchorPane

    @FXML
    lateinit var files: TabPane

    fun addFile()
    {
        var loader = FXMLLoader(this.javaClass.classLoader.getResource("graphics/subcomps/file.fxml"))

        var newFile = Tab("Untitled", loader.load())

        loader.getController<FileController>().script_view = script_view
        loader.getController<FileController>().file = newFile

        loader.getController<FileController>().establishFile()

        files.tabs.add(newFile)
    }

}