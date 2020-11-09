package com.enteld.core.output

import com.enteld.core.token.Token

interface DumpWarnings {
    val warnings: MutableMap<Token, String>
    fun printWarnings(path: String): Boolean
}