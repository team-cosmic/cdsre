package cdsre

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.util.Duration
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.model.StyleSpans
import org.intellij.lang.annotations.Pattern
import org.reactfx.inhibeans.collection.Collections


class FileController: Initializable {

	lateinit var script_view: AnchorPane
	lateinit var file: Tab

	lateinit var script_area: CodeArea

	@FXML
	lateinit var container: AnchorPane

	private val KEYWORDS = arrayOf(
		"placeholder"
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

		script_area.isWrapText = true

		println(container.width)
		println(container.height)
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
}