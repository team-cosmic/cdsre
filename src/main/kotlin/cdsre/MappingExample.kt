package cdsre

import cdsre.files.ROM
import cdsre.files.mapping.Mapping
import cdsre.utils.Constants
import cdsre.workspace.Workspace
import java.io.File
import java.io.FileNotFoundException

// Call this to copy the current mappings into the expected location
fun setupLocalAppdata() {
    File(Constants.CDSRE_PATH + "\\mappings\\Platinum").mkdirs()
    File("./src/main/resources/mappings").copyRecursively(File(Constants.CDSRE_PATH + "\\mappings\\Platinum"), true)
}

// Pass this the string location of your ROM
fun example(romLoc: String) {
    val rom = ROM.loadROM(File(romLoc))
    Workspace.currentROM = rom
    // Requires
    val mapping = Mapping.getMapping("Platinum") ?: throw FileNotFoundException()

    val pokemon = mapping.pokemon ?: throw FileNotFoundException()
    println(pokemon.getEntry(1).getHP())
}
