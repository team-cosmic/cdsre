package cdsre

import cdsre.files.Project
import cdsre.files.ROM
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import java.net.URL
import java.util.*
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import java.awt.Desktop
import java.net.URI
import javafx.stage.FileChooser
import java.io.File
import java.util.Collections.addAll






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
    lateinit var menuitem_open: MenuItem

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
    lateinit var menuitem_viewscript: MenuItem

    @FXML
    lateinit var menuitem_about: MenuItem

    @FXML
    lateinit var menuitem_discord: MenuItem

    @FXML
    lateinit var main: SplitPane

    @FXML
    lateinit var leftpanel: AnchorPane

    @FXML
    lateinit var view: ScrollPane

    @FXML
    lateinit var primaryview: AnchorPane

    @FXML
    lateinit var rightpanel: AnchorPane

    @FXML
    lateinit var leftstatus: Label

    @FXML
    lateinit var bottom: Pane

    @FXML
    lateinit var rightstatus: Label

    /**
     * Quits the program.
     */
    @FXML
    fun quit(event: ActionEvent) {
        Platform.exit()
    }

    @FXML
    fun switchView(event: ActionEvent) {
        println("Loading " + (event.source as MenuItem).text + " View")

        var loader: FXMLLoader? = null
        when((event.source as MenuItem).text)
        {
            menuitem_viewscript.text -> loader = FXMLLoader(this.javaClass.classLoader.getResource("view_script.fxml"))
        }

        this.primaryview = AnchorPane(loader!!.load())
        this.primaryview.prefWidthProperty().bind(this.view.widthProperty())
        this.primaryview.prefHeightProperty().bind(this.view.heightProperty())
        this.view.content = primaryview
        this.leftstatus.text = (event.source as MenuItem).text
    }

    @FXML
    fun openLink(event: ActionEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            when((event.source as MenuItem).text)
            {
                menuitem_about.text -> Desktop.getDesktop().browse(URI("https://github.com/team-cosmic/cdsre"))
                menuitem_discord.text -> Desktop.getDesktop().browse(URI("https://discord.gg/eusTxfA"))
            }
        }
    }

    @FXML
    fun openFile(event: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.title = "Open File"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Nintendo DS ROM", "*.nds"),
            FileChooser.ExtensionFilter("Cosmic ROM Project", "*.crp")
        )

        val file = fileChooser.showOpenDialog(ClientApp.globalStage)

        // If file is a .nds, create a new project wrapping around a ***copy*** of it.
        // If it is a .crp, do some extraction
        var project: Project? = null
        if (file.extension == "crp") {
            project = Project.loadProject(file)
        } else if (file.extension == "nds") {
            var originalRom: ROM = ROM.loadROM(file)
            // TODO: copy old ROM to new ROM. Open project creation menu here
            var newRom: ROM = originalRom
            project = Project.createProject("", newRom)
        }

        // TODO: Set active project to project
    }
}