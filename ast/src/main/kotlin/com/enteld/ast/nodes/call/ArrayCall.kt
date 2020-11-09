package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.expression.TokenNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ArrayCall: BaseNode() {

    private var id: TokenNode? = null
    fun getId() = id

    private var call: CallNode? = null
    fun getCall() = call

    override fun parse(): Boolean {
        when (tokenManager.peekType()){

            Token.Type.LiteralInt -> {
                parseWrapper(TokenNode())?.let {
                    id = it
                }?:errorWrapper<ArrayCall>(message = "Ожидался литерал Int")
            }

            Token.Type.Identifier -> {
                parseWrapper(CallNode())?.let {
                    call = it
                } ?: errorWrapper<ArrayCall>(message = "Произошла ошибка при пасинге CallNode")
            }

            else -> {
                errorWrapper<ArrayCall>(message = "Ожидался Element")
            }
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}