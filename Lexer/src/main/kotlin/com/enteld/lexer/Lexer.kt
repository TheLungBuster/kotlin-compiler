package com.enteld.lexer

import com.enteld.core.output.DumpPrint
import com.enteld.core.token.Token
import com.enteld.paramsreader.Params
import org.koin.core.KoinComponent

class Lexer() : KoinComponent, DumpPrint {
    override val errors: MutableMap<Token, String> = mutableMapOf()
    override val warnings: MutableMap<Token, String> = mutableMapOf()

    override fun printDump(params: List<Params>, path: String) {
        TODO("not implemented")
    }
}