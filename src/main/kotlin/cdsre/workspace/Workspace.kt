package cdsre.workspace

import cdsre.files.Project
import javafx.stage.Stage

object Workspace {

    lateinit var globalStage: Stage

    var currentProject: Project? = null
        set(value) {
            globalStage.title = value!!.name + "| CDSRE v1.0"
            field = value
        }
}