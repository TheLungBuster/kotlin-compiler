package com.enteld.core.output

import com.enteld.core.token.Token

interface DumpErrors {
    val errors: MutableMap<Token, String>
    fun printErrors(path: String): Boolean
}