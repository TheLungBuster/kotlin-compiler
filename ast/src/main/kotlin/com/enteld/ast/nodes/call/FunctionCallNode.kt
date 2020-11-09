package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.interfaces.HasIdentifier
import com.enteld.ast.nodes.scopes.FunctionArgumentsScopeNode
import com.enteld.ast.nodes.scopes.ScopeNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class FunctionCallNode(
    override var identifier: Token
) : BaseNode(), HasIdentifier {

    private var arguments: FunctionArgumentsScopeNode? = null
    fun getArguments() = arguments

    private var scope: ScopeNode? = null
    fun getScope() = scope

    override fun parse(): Boolean {
        skipThrow<FunctionCallNode>(Token.Type.LParen, message = "Ожидался символ '('")
        if (tokenManager.peekType() == Token.Type.RParen) {
            skipThrow<FunctionCallNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        } else {
            parseWrapper(FunctionArgumentsScopeNode())?.let {
                arguments = it
            } ?: errorWrapper<FunctionCallNode>(message = "Произошла ошибка при парсинге FunctionArgumentsScopeNode")
            skipThrow<FunctionCallNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}