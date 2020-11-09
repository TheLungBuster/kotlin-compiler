package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ParamNode: BaseNode() {

    private lateinit var identifier: Token
    fun getIdentifier() = identifier

    private lateinit var paramType: Token
    fun getParamType() = paramType

    override fun parse(): Boolean {
        if (tokenManager.peekType() == Token.Type.Identifier) {
            identifier = tokenManager.get()
        } else errorWrapper<ParamNode>(message = "Ожидался Identifier")
        skipIf<ParamNode>(Token.Type.Colon, condition = true, message = "Ожидался символ ':'")
        when (tokenManager.peekType()) {
            Token.Type.TypeInt,
            Token.Type.TypeDouble,
            Token.Type.TypeString,
            Token.Type.TypeBoolean,
            Token.Type.TypeAny,
            Token.Type.Identifier   -> paramType = tokenManager.get()
            else -> errorWrapper<ParamNode>(message = "Ожидался тип параметра")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}