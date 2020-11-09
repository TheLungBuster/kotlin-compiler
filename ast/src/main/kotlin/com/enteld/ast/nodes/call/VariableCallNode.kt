package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.interfaces.HasIdentifier
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class VariableCallNode(
    override var identifier: Token
) : BaseNode(), HasIdentifier {

    private var callSubject: BaseNode? = null
    fun getCallSubject() = callSubject

    override fun parse(): Boolean {
        when (tokenManager.peekType()) {
            Token.Type.Dot -> {
                skipThrow<VariableCallNode>(Token.Type.Dot, message = "Ожидался символ '.'")
                parseWrapper(CallNode())?.let {
                    callSubject = it
                } ?: errorWrapper<VariableCallNode>(message = "Произошла ошибка при пасинге CallNode")
            }

            Token.Type.LBox -> {
                skipThrow<VariableCallNode>(Token.Type.LBox, message = "Ожидался символ '['")
                parseWrapper(ArrayCall())?.let {
                    callSubject = it
                } ?: errorWrapper<VariableCallNode>(message = "Произошла ошибка при пасинге ArrayCall")
                skipThrow<VariableCallNode>(Token.Type.RBox, message = "Ожидался символ ']'")
            }
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}