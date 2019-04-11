package cdsre.files

import cdsre.utils.Constants
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@kotlin.ExperimentalUnsignedTypes
class Project private constructor(file: File, var name: String = "CDSRE Project") {

	companion object {

		@JvmStatic
		fun loadProject(file: File): Project {
			return Project(file)
		}

		@JvmStatic
		fun createProject(filename: String, name: String): Project {
			val file = File(filename)
			return createProject(file, name)
		}

		@JvmStatic
		fun createProject(file: File, name: String): Project {
			return Project(file, name)
		}
	}

	var version: String = Constants.CURRENT_VERSION

	var home: String = "./"
	var root: String = "root/"

	init {
		if(!file.exists()) {
			file.createNewFile()
		} else {
			val projectInfo = parseXML(file)

			var loadedVersion = projectInfo["version"]
			if(loadedVersion == null) {
				System.err.println("No project version. Project may not work correctly")
			}

			if(loadedVersion == null || loadedVersion != Constants.CURRENT_VERSION) {
				println("Loading old project file. Handle that")
				loadedVersion = Constants.CURRENT_VERSION
			}

			version = loadedVersion
			name = projectInfo.getOrDefault("name", name)

			home = projectInfo.getOrDefault("paths.home", home)
			root = projectInfo.getOrDefault("paths.root", root)
		}
	}

	protected fun parseXML(file: File): Map<String, String> {
		val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
		val docBuilder = factory.newDocumentBuilder()
		val document = docBuilder.parse(file)
		val root = document.documentElement

		root.normalize()

		val out: MutableMap<String, String> = HashMap()

		for(i in 1..root.childNodes.length) {
			val el = root.childNodes.item(i)

			if(el !is Element)
				continue

			val name = el.tagName

			if(el.childNodes.length > 1) {
				for(j in 1..el.childNodes.length) {
					val e = el.childNodes.item(j)
					if(e !is Element)
						continue

					out[name + "." + e.tagName] = e.textContent
				}
			} else {
				out[name] = el.textContent
			}
		}

		return out
	}

	fun save(file: File) {
		val docFactory = DocumentBuilderFactory.newInstance()
		val docBuilder = docFactory.newDocumentBuilder()

		// root element
		val doc = docBuilder.newDocument()
		val rootElement = doc.createElement("proj")
		doc.appendChild(rootElement)

		// Version element
		val versionElement = doc.createElement("version")
		versionElement.appendChild(doc.createTextNode(version))
		rootElement.appendChild(versionElement)

		// Project name
		val nameElement = doc.createElement("name")
		nameElement.appendChild(doc.createTextNode(name))
		rootElement.appendChild(nameElement)

		// Project paths
		val paths = doc.createElement("paths")
		val homePath = doc.createElement("home")
		homePath.appendChild(doc.createTextNode(home))
		val rootPath = doc.createElement("root")
		rootPath.appendChild(doc.createTextNode(root))
		paths.appendChild(homePath)
		paths.appendChild(rootPath)
		rootElement.appendChild(paths)

		// Write to file
		val transformerFactory = TransformerFactory.newInstance()
		val transformer = transformerFactory.newTransformer()
		val source = DOMSource(doc)
		val result = StreamResult(file)

		transformer.transform(source, result)
	}
}