package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.expression.BooleanExpressionNode
import com.enteld.ast.nodes.scopes.ScopeNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class WhileNode : BaseNode() {

    private lateinit var booleanExpressionNode: BooleanExpressionNode
    fun getExpressionNode() = booleanExpressionNode

    private var scope = ScopeNode()
    fun getScope() = scope

    override fun parse(): Boolean {
        skipThrow<WhileNode>(Token.Type.KeywordWhile, Token.Type.LParen, message = "Ожидался символ '('")
        parseWrapper(BooleanExpressionNode())?.let {
            booleanExpressionNode = it
        } ?: errorWrapper<WhileNode>(message = "Произошла ошибка при парсинге ExpressionNode")
        skipThrow<WhileNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        parseWrapper(ScopeNode())?.let {
            scope = it
        } ?: errorWrapper<WhileNode>(message = "Произошла ошибка при парсинге ScopeFieldNode")
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}