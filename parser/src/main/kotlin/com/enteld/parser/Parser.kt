package com.enteld.parser

import com.enteld.ast.nodes.XMLVisitorImpl
import com.enteld.ast.nodes.scopes.RootScopeNode
import com.enteld.core.output.DumpPrint
import com.enteld.core.token.Token
import com.enteld.core.token.TokenManager
import com.enteld.paramsreader.Params
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class Parser(tokens: List<Token>): KoinComponent, DumpPrint {

    init {
        get<TokenManager>(parameters = { parametersOf(tokens) })
    }

    val tree = RootScopeNode()

    val xmlVisitor: XMLVisitorImpl by inject()

    fun parse() = tree.parse()

    override fun printDump(params: List<Params>, path: String) {
        //val param = params.find { it == Params.DUMP_AST }
        //if (param != null) {
        //    tree.accept(xmlVisitor)
        //    xmlVisitor.xmlTreeColored()
        //}
        if (params.contains(Params.DUMP_AST)) {
            tree.accept(xmlVisitor)
            xmlVisitor.xmlTreeColored()
        }
    }
}