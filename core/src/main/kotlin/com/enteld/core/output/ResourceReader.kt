package com.enteld.core.output

import java.util.*

class ResourceReader(lang: String) {
    private val resourceBundle = ResourceBundle.getBundle("strings", Locale(lang))
    fun getResource(name: String): String = resourceBundle.getString(name)
}