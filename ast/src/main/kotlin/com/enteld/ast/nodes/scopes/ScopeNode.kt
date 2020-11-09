package com.enteld.ast.nodes.scopes

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ScopeNode: BaseNode() {

    private val fields = mutableListOf<BaseNode>()
    fun getFields() = fields.toList()

    override fun parse(): Boolean {
        skipThrow<ScopeNode>(Token.Type.LFigured, message = "Ожидался символ {")
        while (tokenManager.peekType() != Token.Type.RFigured) {
            parseWrapper(ScopeFieldNode())?.let {
                fields.add(it)
            } ?: errorWrapper<ScopeNode>(message = "При парсинге ScopeFieldNode произошла ошибка")
        }
        skipThrow<ScopeNode>(Token.Type.RFigured, message = "Ожидался символ }")
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}