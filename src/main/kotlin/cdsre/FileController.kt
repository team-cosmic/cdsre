package cdsre

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.stage.Popup
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.model.StyleSpans
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import org.fxmisc.richtext.event.MouseOverTextEvent
import java.awt.SystemColor.text
import javafx.scene.input.KeyCombination.SHIFT_DOWN
import javafx.scene.input.KeyCombination.CONTROL_DOWN
import javafx.scene.input.KeyEvent
import org.fxmisc.wellbehaved.event.EventPattern.keyPressed


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

	override fun initialize(p0: URL?, p1: ResourceBundle?) {
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

		val popup = Popup()
		val popupMsg = Label()
		popupMsg.style = "-fx-background-color: black;" +
				"-fx-text-fill: white;" +
				"-fx-padding: 5;"
		popup.content.add(popupMsg)

		script_area.addEventFilter(KeyEvent.KEY_PRESSED) {
			if(popup.isShowing)
			{
				popupMsg.text += it.text
				println(it.character)
				println(popupMsg.text)
			}
		}

		script_area.mouseOverTextDelay = Duration.ofMillis(500)
		script_area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN) {

			val chIdx = it.characterIndex
			val pos = it.screenPosition
			popupMsg.text = this.getWordAround(script_area.text, chIdx)
			popup.show(script_area, pos.x, pos.y + 10)
		}
		script_area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END) { popup.hide()  }

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
			val styleClass = (if (matcher.group("KEYWORD") != null)
				"keyword"
			else if (matcher.group("PAREN") != null)
				"paren"
			else if (matcher.group("BRACE") != null)
				"brace"
			else if (matcher.group("BRACKET") != null)
				"bracket"
			else if (matcher.group("SEMICOLON") != null)
				"semicolon"
			else if (matcher.group("STRING") != null)
				"string"
			else if (matcher.group("COMMENT") != null)
				"comment"
			else
				null)!! /* never happens */
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

		println("Given Index: $startIndex")
		if (source != null)
		{
			for (i in startIndex downTo 0)
			{
				if (Character.isWhitespace(source[i]) || i == 0) {
					startPosition = i
					println("Start: " + startPosition)
					break
				}
			}
			for (j in startIndex..source.length)
			{
				if (j == source.length || Character.isWhitespace(source[j])) {
					endPosition = j
					println("End: " + startPosition)
					break
				}
			}
		}

		println(source.length)
		return source.substring(startPosition, endPosition)
	}
}