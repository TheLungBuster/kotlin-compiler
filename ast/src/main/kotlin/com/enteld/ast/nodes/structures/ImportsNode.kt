package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ImportsNode : BaseNode() {

    private var imports = mutableListOf<ImportNode>()
    fun getImports() = imports

    override fun parse(): Boolean {
        while (tokenManager.peekType() == Token.Type.KeywordImport) {
           parseWrapper(ImportNode())?.let {
               imports.add(it)
           } ?: errorWrapper<ImportsNode>(message = "При парсинге ImportNode произошла ошибка")
       }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}