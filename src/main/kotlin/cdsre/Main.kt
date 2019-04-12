package cdsre

import cdsre.files.NARC
import cdsre.files.ROM
import javafx.application.Application
import java.io.File

fun main() {
//    val nloc = File("C:\\ProgrammingFiles\\NDS_Hacking\\Pokemon-Platinum-Version-(US)\\root\\poketool\\personal\\personal.narc")
//    val narc = NARC.loadNARC(nloc)
//    println(narc)

    val loc = File("C:\\ProgrammingFiles\\NDS_Hacking\\Pokemon-Platinum-Version-(US)\\3541 - Pokemon Platinum Version (US)(XenoPhobia).nds")
    val rom = ROM.loadROM(loc)
    println(rom.getFile("poketool/personal/personal.narc"))
    println(rom.getNarc("poketool/personal/personal.narc"))

	Application.launch(ClientApp::class.java)
}