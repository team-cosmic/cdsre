package cdsre.workspace

import cdsre.files.Project
import cdsre.files.ROM
import cdsre.files.mapping.Mapping
import javafx.stage.Stage

object Workspace {

    lateinit var globalStage: Stage

    var currentProject: Project? = null
        set(value) {
            globalStage.title = value!!.name + "| CDSRE v1.0"
            field = value
        }

    var currentROM: ROM? = null

    var currentMapping: Mapping? = null

}