package cdsre

import javafx.event.ActionEvent
import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import java.awt.event.MouseEvent


class CDSREController: Initializable {

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

    }

    @FXML
    lateinit var program: VBox

    @FXML
    lateinit var menubar: MenuBar


    @FXML
    lateinit var menu_file: Menu

    @FXML
    lateinit var menuitem_openrom: MenuItem

    @FXML
    lateinit var menu_openrecent: Menu

    @FXML
    lateinit var menuitem_close: MenuItem

    @FXML
    lateinit var menuitem_saverom: MenuItem

    @FXML
    lateinit var menuitem_saveas: MenuItem

    @FXML
    lateinit var menuitem_prefs: MenuItem

    @FXML
    lateinit var menuitem_quit: MenuItem

    @FXML
    lateinit var menu_edit: Menu

    @FXML
    lateinit var menu_view: Menu

    @FXML
    lateinit var menu_help: Menu

    @FXML
    lateinit var menuitem_help: MenuItem

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

    @FXML
    fun quit(event: ActionEvent) {
        System.exit(0)
    }
}