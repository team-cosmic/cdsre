package cdsre.files.mapping

class ItemNarcMapping(location: String, entries: Map<String, EntryDef>) : NarcMapping("item", location, listOf(), entries)