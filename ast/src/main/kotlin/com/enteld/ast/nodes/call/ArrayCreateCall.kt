package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.scopes.ScopeNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ArrayCreateCall: BaseNode() {

    private lateinit var callSubject: BaseNode
    fun getCallSubject() = callSubject

    private lateinit var size: Token

    override fun parse(): Boolean {
        skipThrow<ArrayCreateCall>(Token.Type.TypeList, message = "Ожидался List")
        skipThrow<ArrayCreateCall>(Token.Type.LParen, message = "Ожидался '('")
        if (tokenManager.peekType() == Token.Type.LiteralInt ||
            tokenManager.peekType() == Token.Type.Identifier
        ) {
            size = tokenManager.get()
            skipThrow<ArrayCreateCall>(Token.Type.RParen, message = "Ожидался ')'")
        } else {
            errorWrapper<ArrayCreateCall>(message = "Ожидалось обьявление размера массива")
        }
        if (tokenManager.peekType() == Token.Type.LFigured) {
            parseWrapper(ScopeNode())?.let {
                callSubject = it
            }?: errorWrapper<ArrayCreateCall>(message = "Произошла ошибка при парсинге ScopeNode")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}