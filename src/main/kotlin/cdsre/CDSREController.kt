package cdsre

import cdsre.controllers.project.ProjectSetup
import cdsre.files.Project
import cdsre.files.ROM
import cdsre.workspace.Workspace
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.FileChooser
import java.awt.Desktop
import java.net.URI
import java.util.*
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import java.lang.Exception
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.collections.HashMap


class CDSREController: Initializable {

	var directory: String = System.getProperty("user.home").replace("\\", "/")
	var lastDirectory: String = ""

	override fun initialize(p0: URL?, p1: ResourceBundle?) {

		/** CONTROLLER SETUP **/
		var names = arrayOf("pokemon", "text", "event", "script", "mapheaders", "matrix", "map")

		for(name in names)
		{
			var tempLeft = FXMLLoader(this.javaClass.classLoader.getResource("graphics/master/master_$name.fxml"))
			var tempMid = FXMLLoader(this.javaClass.classLoader.getResource("graphics/view/view_$name.fxml"))
			var tempRight = FXMLLoader(this.javaClass.classLoader.getResource("graphics/details/details_$name.fxml"))

			var tempMulti = MultiController(tempLeft, tempMid, tempRight)
			controllers[name] = tempMulti
		}

		/** COMMAND LINE **/
		console.style = "-fx-font-family: monospace"
		console.text = "$directory> "
		console.setOnKeyPressed { event ->

			//TODO: implement for non-Windows users. :P
			if(event.code == KeyCode.ENTER) {
				val builder = ProcessBuilder()
				var inArr = console.text.split("\n")
				var input = inArr[inArr.size - 2].replace("$directory>", "").trim()
				builder.command("cmd.exe", "/c", input)

				if(input.startsWith("cd"))
				{
					lastDirectory = directory
					directory = input.substring(3)
				}
				builder.directory(File(directory))

				try{
					val process = builder.start()

					var line: String? = ""
					var buf = BufferedReader(InputStreamReader(process.inputStream))
					while(line != null)
					{
						line = buf.readLine()

						if(line != null)
						{
							console.appendText(line + "\n")
						}else {
							println("Line was null!")
						}
					}
				}catch (e: Exception) {
					val sw = StringWriter()
					val pw = PrintWriter(sw)
					e.printStackTrace(pw)
					console.appendText("\n" + sw.toString())
					directory = lastDirectory
				}
				console.appendText("$directory> ")
				console.positionCaret(console.text.length)
			}
		}
	}

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
	lateinit var menuitem_viewpokemon: MenuItem

	@FXML
	lateinit var menuitem_viewmapheaders: MenuItem

	@FXML
	lateinit var menuitem_viewmatrix: MenuItem

	@FXML
	lateinit var menuitem_viewmap: MenuItem

	@FXML
	lateinit var menuitem_viewtext: MenuItem

	@FXML
	lateinit var menuitem_viewscript: MenuItem

	@FXML
	lateinit var menuitem_viewevent: MenuItem

	@FXML
	lateinit var menuitem_about: MenuItem

	@FXML
	lateinit var menuitem_discord: MenuItem

	@FXML
	lateinit var menuitem_github: MenuItem

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
	lateinit var console: TextArea

	/**
	 * Quits the program.
	 */
	@FXML
	fun quit(event: ActionEvent) {
		Platform.exit()
	}

	@FXML
	fun switchView(event: ActionEvent) {
		println("Loading " + (event.source as MenuItem).id + " View")
		var viewToLoad = (event.source as MenuItem).id

		var masterpanel = AnchorPane(controllers[viewToLoad]!!.master)
		masterpanel.prefWidthProperty().bind(this.leftpanel.widthProperty())
		masterpanel.prefHeightProperty().bind(this.leftpanel.heightProperty())
		leftpanel.children.setAll(masterpanel)

		var primaryview = AnchorPane(controllers[viewToLoad]!!.view)
		primaryview.prefWidthProperty().bind(this.view.widthProperty())
		primaryview.prefHeightProperty().bind(this.view.heightProperty())
		view.content = primaryview
		this.leftstatus.text = (event.source as MenuItem).text

		var detailpanel = AnchorPane(controllers[viewToLoad]!!.details)
		detailpanel.prefWidthProperty().bind(this.rightpanel.widthProperty())
		detailpanel.prefHeightProperty().bind(this.rightpanel.heightProperty())
		rightpanel.children.setAll(detailpanel)
	}

	@FXML
	fun openLink(event: ActionEvent) {
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			when((event.source as MenuItem).text) {
				menuitem_github.text -> Desktop.getDesktop().browse(URI("https://github.com/team-cosmic/cdsre"))
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
			FileChooser.ExtensionFilter("Cosmic ROM Project", "*.crp"),
			FileChooser.ExtensionFilter("CDSRE Files", "*.nds", "*.crp")
		)

		val file = fileChooser.showOpenDialog(Workspace.globalStage)
			?: return //If file is null, then no file was selected; simply do nothing

		println(file.extension)

		if(file.extension == "crp") {
			Workspace.currentProject = Project.loadProject(file)
		} else if(file.extension == "nds") {
			var originalRom: ROM = ROM.loadROM(file)
			var projectName: String = ""
			// TODO: Open project creation dialog here
			// TODO: Unpack ROM into project location
			//project = Project.createProject("", projectName)
		} else {
			System.err.println("An invalid file has somehow been loaded? Nixing file!")
			return
		}

		// TODO: Set active project to project
	}

	@FXML
	fun addFile(event: ActionEvent) {

		//TODO: Make this more safe. Script view is not certain to be open at this point.

		var loader = FXMLLoader(this.javaClass.classLoader.getResource("graphics/subcomps/file.fxml"))

		var newFile = Tab("", loader.load())

		loader.getController<FileController>().script_view = view.content as AnchorPane
		loader.getController<FileController>().file = newFile

		var files = loader.getController<FileController>().script_view.children[0].lookup("#files") as TabPane

		loader.getController<FileController>().establishFile()

		newFile.setOnClosed {
			loader.getController<FileController>().closeFile()
		}

		files.tabs.add(newFile)

		newFile.text = "Untitled " + (newFile.tabPane.tabs.indexOf(newFile) + 1)
	}

	@FXML
	fun openProjectSetup(event: ActionEvent) = ProjectSetup().start()

	companion object {
		@JvmStatic
		var controllers: HashMap<String, MultiController> = HashMap()
	}
}