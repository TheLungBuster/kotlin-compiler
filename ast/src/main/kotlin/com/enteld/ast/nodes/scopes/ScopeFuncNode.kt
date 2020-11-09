package com.enteld.ast.nodes.scopes

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.structures.ParamNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ScopeFuncNode(
    private val params: List<ParamNode>,
    private val returnType: Token.Type
): BaseNode() {

    private var fields = mutableListOf<BaseNode>()
    fun getFields() = fields.toList()

    override fun parse(): Boolean {
        skipThrow<ScopeFuncNode>(Token.Type.LFigured, message = "Ожидался символ '{'")
        while (tokenManager.peekType() != Token.Type.RFigured) {
            parseWrapper(ScopeFieldNode())?.let {
                fields.add(it)
            } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге ScopeFieldsNode")
        }
        skipThrow<ScopeFuncNode>(Token.Type.RFigured, message = "Ожидался символ '}'")
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}