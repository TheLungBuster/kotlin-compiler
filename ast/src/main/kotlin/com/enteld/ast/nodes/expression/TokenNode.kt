package com.enteld.ast.nodes.expression

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class TokenNode: BaseNode() {

    private lateinit var token: Token
    fun getToken() = token

    override fun parse(): Boolean {
        token = tokenManager.get()
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}