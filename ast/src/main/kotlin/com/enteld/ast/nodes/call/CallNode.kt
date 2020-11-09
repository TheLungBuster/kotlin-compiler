package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class CallNode : BaseNode() {

    private lateinit var identifier: Token
    fun getIdentifier() = identifier

    private var callSubject: BaseNode? = null
    fun getCallSubject() = callSubject

    override fun parse(): Boolean {
        if (tokenManager.peekType() == Token.Type.Identifier) {
            identifier = tokenManager.peek()
        } else errorWrapper<CallNode>(message = "Ожидался Identifier")
        skipThrow<CallNode>(Token.Type.Identifier, message = "Ожидался Identifier")

        when (tokenManager.peekType()) {
            Token.Type.LParen -> {
                parseWrapper(FunctionCallNode(identifier))?.let {
                    callSubject = it
                } ?: errorWrapper<CallNode>(message = "Произошла ошибка при парсинге FunctionCallNode")
            }
            Token.Type.LBox -> {
                parseWrapper(VariableCallNode(identifier))?.let {
                    callSubject = it
                } ?: errorWrapper<CallNode>(message = "Произошла ошибка при парсинге FunctionCallNode")
            }
            Token.Type.Dot -> {
                skipThrow<CallNode>(Token.Type.Dot, message = "Ожидался символ '.'")
                parseWrapper(CallNode())?.let {
                    callSubject = it
                } ?: errorWrapper<CallNode>(message = "Произошла ошибка при парсинге CallNode")
            }
            else -> return true
        }
        if (tokenManager.peekType() == Token.Type.Dot) {
            skipThrow<CallNode>(Token.Type.Dot, message = "Ожидался символ '.'")
            parseWrapper(CallNode())?.let {
                callSubject = it
            } ?: errorWrapper<CallNode>(message = "Произошла ошибка при парсинге CallNode")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}