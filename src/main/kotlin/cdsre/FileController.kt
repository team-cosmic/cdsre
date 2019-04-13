package cdsre

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.stage.Popup
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.event.MouseOverTextEvent
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.model.StyleSpans
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.regex.Pattern


class FileController: Initializable {

	lateinit var script_view: AnchorPane
	lateinit var file: Tab

	lateinit var script_area: CodeArea

	@FXML
	lateinit var container: AnchorPane

	private val KEYWORDS = arrayOf(
		""
	)

	private val KEYWORD_PATTERN = "\\b(" + KEYWORDS.joinToString("|") + ")\\b"
    private val PAREN_PATTERN = "\\(|\\)"
    private val BRACE_PATTERN = "\\{|\\}"
    private val BRACKET_PATTERN = "\\[|\\]"
    private val SEMICOLON_PATTERN = "\\;"
    private val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
    private val COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"

    private val PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
		+ "|(?<PAREN>" + PAREN_PATTERN + ")"
		+ "|(?<BRACE>" + BRACE_PATTERN + ")"
		+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
		+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")"
		+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
)

	var masterData: ObservableList<String> = FXCollections.observableArrayList<String>()

	var searchQuery: String = ""
	var queryIndex: Int = 0

	override fun initialize(p0: URL?, p1: ResourceBundle?) {

		//TODO: Populate these with real command names instead
		for(i in 0..100)
		{
			masterData.add("Item$i")
		}
	}

	fun establishFile() {
		file.content = container

		// Create text area for scripting
		script_area = CodeArea()
		script_area.style = ".code-area"

		script_area.paragraphGraphicFactory = LineNumberFactory.get(script_area)

		var cleanUpWhenDone = script_area.multiPlainChanges().successionEnds(Duration.ofMillis(500)).subscribe {
			script_area.setStyleSpans(0, computeHighlighting(script_area.text))
		}

		//TODO: These should be bound to the tab.
		script_area.prefWidthProperty().bind(container.widthProperty())
		script_area.prefHeightProperty().bind(container.heightProperty())

		val filteredList = FilteredList(masterData) { data -> true }

		val popup = Popup()
		val popupMsg = ListView<String>()
		popupMsg.items = filteredList

		//TODO: Move these into main css
		popupMsg.style = "-fx-background-color: #444444;" +
				"-fx-text-fill: white;" +
				"-fx-padding: 15;"
		popup.content.add(popupMsg)

		popupMsg.setOnMouseClicked {
			var temp = popupMsg.selectionModel.selectedItem + " "

			script_area.replaceText(queryIndex, script_area.caretPosition, temp)

			if(popup.isShowing) {
				popup.hide()
			}
			it.consume()
		}

		popupMsg.setOnKeyPressed {
			if(it.code == KeyCode.ENTER)
			{
				var temp = popupMsg.selectionModel.selectedItem + " "

				script_area.replaceText(queryIndex, script_area.caretPosition, temp)

				if(popup.isShowing) {
					popup.hide()
				}
				it.consume()
			}
		}

		popup.focusedProperty().addListener { arg0, oldPropertyValue, newPropertyValue ->
			if (!newPropertyValue!!) {
				popup.hide()
			}
		}

		val s = script_area.plainTextChanges().subscribe { tc ->

			if(tc.inserted != " " && tc.inserted != "\n")
			{
				if (tc.removed.isNotEmpty() && tc.inserted.isEmpty())
				{
					searchQuery = this.getWordAround(script_area.text, tc.position - 1).trim()
				} else if (tc.inserted.isNotEmpty() && tc.removed.isEmpty())
				{
					searchQuery = this.getWordAround(script_area.text, tc.position).trim()
				}

				println(searchQuery)

				filteredList.setPredicate{
					if (searchQuery.isEmpty()){
						true
					}
					var lowerCaseSearch = searchQuery.toLowerCase()

					it.toLowerCase().contains(lowerCaseSearch)
				}

				if(filteredList.size == 0)
				{
					popup.hide()
				}else {
					if(!popup.isShowing)
					{
						popup.show(script_area, script_area.caretBounds.get().centerX, script_area.caretBounds.get().centerY + 10)
						queryIndex = script_area.caretPosition - 1
					}
				}
			}else {
				if(popup.isShowing)
				{
					popup.hide()
				}
			}
		}

		//TODO: Clean this up
		val popup2 = Popup()
		val popupMsg2 = Label()
		script_area.mouseOverTextDelay = Duration.ofMillis(500)
		script_area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN) {

			val chIdx = it.characterIndex
			val pos = it.screenPosition
			popupMsg2.text = this.getWordAround(script_area.text, chIdx)
			popup2.show(script_area, pos.x, pos.y + 10)
		}
		script_area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END) { popup2.hide()  }

		popupMsg2.style = "-fx-background-color: black;" +
				"-fx-text-fill: white;" +
				"-fx-padding: 5;"
		popup2.content.add(popupMsg2)

		script_area.isWrapText = true

		container.children.add(script_area)
	}

	private fun applyHighlighting(highlighting: StyleSpans<Collection<String>>) {
		script_area.setStyleSpans(0, highlighting)
	}

	private fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
		val matcher = PATTERN.matcher(text)
		var lastKwEnd = 0
		val spansBuilder = StyleSpansBuilder<Collection<String>>()
		while (matcher.find()) {
			val styleClass = (when {
				matcher.group("KEYWORD") != null -> "keyword"
				matcher.group("PAREN") != null -> "paren"
				matcher.group("BRACE") != null -> "brace"
				matcher.group("BRACKET") != null -> "bracket"
				matcher.group("SEMICOLON") != null -> "semicolon"
				matcher.group("STRING") != null -> "string"
				matcher.group("COMMENT") != null -> "comment"
				else -> null
			})!! /* never happens */
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd)
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start())
			lastKwEnd = matcher.end()
		}
		spansBuilder.add(Collections.emptyList(), text.length - lastKwEnd)
		return spansBuilder.create()
	}

	fun closeFile() {
		println("File closed.")
	}

	private fun getWordAround(source: String, startIndex: Int = 0): String {
		if (startIndex < 0) return ""

		var startPosition = 0
		var endPosition = 0

		println(source)
		for (i in startIndex downTo 0)
		{
			if (i == 0) {
				startPosition = i
				break
			}else if(Character.isWhitespace(source[i])) {
				startPosition = i
				break
			}
		}
		for (j in startIndex..source.length)
		{
			if (j == source.length || Character.isWhitespace(source[j])) {
				endPosition = j
				break
			}
		}

		println(source.length)
		return source.substring(startPosition, endPosition)
	}
}