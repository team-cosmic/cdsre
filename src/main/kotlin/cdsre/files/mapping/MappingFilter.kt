package cdsre.files.mapping

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.XMLFilterImpl
import java.io.File
import java.lang.NumberFormatException
import javax.management.modelmbean.XMLParseException
import javax.xml.parsers.SAXParserFactory

fun <T> safeConvert(func: () -> T): T? {
    return try {
        func()
    } catch (e: Exception) {
        null
    }
}

fun splitFilename(name: String): List<String> {
    return name.split("_", ".")
}

/**
 * Handles the generation of a NarcMapping from a .xml file
 *
 * Note on promises:
 * startElement sets up state,
 * endElement tears it down
 *
 * After an element has ended, its internal state should be reset
 * to its beginning, default or such. It is not the job of startElement
 * to ensure that variables are reset before it starts its work
 */
class MappingFilter : XMLFilterImpl() {

    private var filename = ""

    private var location = ""
    private var functions: MutableList<FunctionDef> = mutableListOf()
    private var entries: MutableMap<String, EntryDef> = mutableMapOf()

    private var inEntries: Boolean = false
    private var inEntry: Boolean = false
    private var entryOffset: Int = 0
    private var subentryOffset: Int = 0

    private var curEntry: EntryDef? = null
    private lateinit var newMap: NarcMapping

    init {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        this.parent = parser.xmlReader
    }

    fun parse(file: File): NarcMapping {
        filename = file.name
        this.parse(InputSource(file.inputStream()))
        return newMap
    }

    override fun startElement(uri: String?, localName: String?, qName: String?, atts: Attributes?) {
        super.startElement(uri, localName, qName, atts)
        when (localName) {
            "mapping" -> {
                location = ""
                functions = mutableListOf()
                entries = mutableMapOf()

                entryOffset = 0
            }
            "location" -> {
                location = atts?.getValue("src") ?: throw XMLParseException("Location element must include a source")
            }
            "function" -> {
                val strIndex = atts?.getValue("index") ?: throw XMLParseException("Function element must include index")
                val index = safeConvert(strIndex::toInt) ?: throw XMLParseException("Function index must be an integer")
                val name = atts.getValue("name") ?: "CMD_0x$index"
                functions.add(FunctionDef(index, name))
            }
            "entries" -> {
                inEntries = true
            }
            "entry" -> {
                if (!inEntries)
                    throw XMLParseException("Entry must be nested within an entries block")

                val name = atts?.getValue("name") ?: throw XMLParseException("Entry element must define a name")
                val strLen = atts.getValue("length") ?: "1"
                val length = safeConvert(strLen::toInt) ?: throw XMLParseException("Entry length must be an integer")

                if (name != "PADDING") {
                    curEntry = EntryDef(name, length, entryOffset)
                    inEntry = true
                }
                entryOffset += length
            }
            "subentry" -> {
                if (!inEntry)
                    throw XMLParseException("Subentry must be nested within an entry block")

                val name = atts?.getValue("name") ?: throw XMLParseException("Subentry element must define a name")
                val strLen = atts.getValue("length") ?: "1"
                val length = safeConvert(strLen::toInt) ?: throw XMLParseException("Sebentry length must be an integer")

                if (name != "PADDING") {
                    curEntry?.addSubEntry(SubentryDef(name, length, subentryOffset))
                }
                subentryOffset += length
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(uri, localName, qName)
        when (localName) {
            "mapping" -> {
                // Create NarcMapping
                val pieces = filename.split("_")
                val type = pieces[1]

                newMap = when (type) {
                    "scripts" -> {
                        ScriptNarcMapping(functions)
                    }
                    "pokemon" -> {
                        PokemonNarcMapping(entries)
                    }
                    "items" -> {
                        ItemNarcMapping(entries)
                    }
                    else -> {
                        NarcMapping(type, functions, entries)
                    }
                }
            }
            "entries" -> {
                inEntries = false
            }
            "entry" -> {
                inEntry = false
                val entry = curEntry ?: return
                entries[entry.name] = entry
                curEntry = null

                subentryOffset = 0
            }
        }
    }

}