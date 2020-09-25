package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.core.token.Token

class ImportsNode : BaseNode() {

    private var imports = mutableListOf<ImportNode>()
    fun getImports() = imports

    override fun parse(): Boolean {
       do {
           parseWrapper(ImportNode())?.let {
               imports.add(it)
           } ?: errorWrapper<ImportsNode>(message = "При парсинге ImportNode произошла ошибка")
       } while (tokenManager.peekType() == Token.Type.KeywordImport)
        return true
    }

    override fun accept() {
        TODO("Not yet implemented")
    }
}