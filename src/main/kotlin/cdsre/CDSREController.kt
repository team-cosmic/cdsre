package kotlinchatapp

import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.MenuBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.layout.AnchorPane
import javafx.scene.control.SplitPane
import javafx.scene.layout.VBox




class CDSREController: Initializable {

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

    }

    @FXML
    lateinit var program: VBox

    @FXML
    lateinit var menubar: MenuBar

    @FXML
    lateinit var main: SplitPane

    @FXML
    lateinit var leftpanel: AnchorPane

    @FXML
    lateinit var view: ScrollPane

    @FXML
    lateinit var rightpanel: AnchorPane

    @FXML
    lateinit var leftstatus: Label

    @FXML
    lateinit var bottom: Pane

    @FXML
    lateinit var rightstatus: Label
}