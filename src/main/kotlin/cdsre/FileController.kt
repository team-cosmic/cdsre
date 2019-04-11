package cdsre

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.model.StyleSpans





class FileController: Initializable {

	lateinit var script_view: AnchorPane
	lateinit var file: Tab

	lateinit var script_area: CodeArea

	@FXML
	lateinit var container: AnchorPane

	private val KEYWORDS = arrayOf(
		"Nope",
		"Nop1",
		"End",
		"Return2",
		"If",
		"If2",
		"CallStandard",
		"Jump",
		"Call",
		"Return",
		"CompareLastResultJump",
		"CompareLastResultCall",
		"SetFlag",
		"ClearFlag",
		"CheckFlag",
		"Copy",
		"SetVar",
		"CopyVar",
		"Message",
		"Message2",
		"WaitButton",
		"CloseMessageOnKeyPress",
		"FreezeMessageBox",
		"ColorMessageBox",
		"TypeMessageBox",
		"NoMapMessageBox",
		"Multi",
		"Multi2",
		"TextScriptMulti",
		"CloseMulti",
		"Multi3",
		"MultiRow",
		"PlayFanfare",
		"PlayFanfare2",
		"WaitFanfare",
		"PlayCry",
		"WaitCry",
		"PlaySound",
		"FadeDefaultMusic",
		"PlayMusic",
		"StopMusic",
		"RestartMusic",
		"SwitchMusic",
		"SwitchMusic2",
		"ApplyMovement",
		"WaitMovement",
		"LockAll",
		"ReleaseAll",
		"Lock",
		"Release",
		"AddPeople",
		"RemovePeople",
		"LockCam",
		"FacePlayer",
		"CheckSpritePosition",
		"CheckPersonPosition",
		"ContinueFollow",
		"FollowHero",
		"GiveMoney",
		"TakeMoney",
		"CheckMoney",
		"ShowMoney",
		"HideMoney",
		"TakeItem",
		"GiveItem",
		"CheckItem",
		"DoubleMessage",
		"GiveStoredPokemon",
		"RecordPokegearNumber",
		"CallEnd",
		"Wifi",
		"OpenPokemonDress",
		"CallEnd2",
		"EndGame",
		"WFC",
		"GivePokemonNickname",
		"FadeScreen",
		"ResetScreen",
		"Warp",
		"HallFameData",
		"WFC1",
		"SetVariableRival",
		"SetVariableAlter",
		"SetVariablePokemon",
		"SetVariableItem",
		"SetVariableAttackItem",
		"SetVariableAttack",
		"SetVariableNumber",
		"SetVariablePokemonNickname",
		"SetVariableObject",
		"SetVariablePokemonStored",
		"CheckStarter",
		"TrainerBattle",
		"LostGoPokecenter",
		"CheckLost",
		"StoreStarter",
		"ChecPokemonGender",
		"WarpLift",
		"CheckFloor",
		"WirelessBattleWait",
		"PokemonContest",
		"CheckGender",
		"HealPokemon",
		"ActivatePokedex",
		"GiveRunningShoes",
		"CheckBadge",
		"EnableBadge",
		"DisableBadge",
		"PrepareDoorAnimation",
		"CloseDoor",
		"MoveDoor",
		"OpenDoor",
		"WaitDoor",
		"VermillionGymAnimation",
		"VermillionGymBin",
		"AzaleaGym",
		"AzaleaGym2",
		"CheckPartyNumber",
		"SetOverworldPosition",
		"ChoosePokemonMenu",
		"ChoosePokemonMenu2",
		"StorePokemonMenu",
		"StorePokemonNumber",
		"CheckIfPokemonTraded",
		"CheckHiroMoneyNumber",
		"OpenMail",
		"CheckMail",
		"ComparePokemonHeight",
		"CheckPokemonHeight",
		"ChoosePokemonDelete",
		"StoreMoveDelete",
		"CheckMoveNumber",
		"DeleteMove",
		"SetVariableMoveDelete",
		"GiveItemStored",
		"ChoosePokemonRem",
		"StoreMoveRem",
		"CheckPokemonTrade",
		"TradeChosenPokemon",
		"StopTrade",
		"CheckRibbon",
		"GiveRibbon",
		"CheckPokemonRibbon",
		"RBattleRecorder",
		"CheckVersion",
		"CheckBoxesNumber",
		"StoreFriendParkResult",
		"ChoosePokemonMenuTrade",
		"LegendaryBattle",
		"StartFriendPark",
		"EndFriendPark",
		"MecScript",
		"MakePhoto",
		"CheckAlbumPhoto",
		"CheckWildBattle2",
		"WildBattle2",
		"OpenLowScreen",
		"CloseLowScreen",
		"OpenLowYesNoBox",
		"Multi4",
		"Multi5",
		"TextScriptMulti4",
		"CloseMulti4",
		"TakeMomMoney",
		"GiveMomMoney",
		"OpenMomMoneyBox",
		"CloseMomMoneyBox",
		"CheckMomMoneyNumber"

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