package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.expression.BooleanExpressionNode
import com.enteld.ast.nodes.scopes.ScopeNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class DoNode : BaseNode() {

    private lateinit var booleanExpressionNode: BooleanExpressionNode
    fun getExpressionNode() = booleanExpressionNode

    private var scope = ScopeNode()
    fun getScope() = scope

    override fun parse(): Boolean {
        skipThrow<DoNode>(Token.Type.LFigured, message = "Ожидался символ '{'")
        parseWrapper(ScopeNode())?.let {
            scope = it
        } ?: errorWrapper<DoNode>(message = "Произошла ошибка при парсинге ScopeFieldNode")
        skipThrow<DoNode>(Token.Type.KeywordWhile, Token.Type.LParen, message = "Ожидался символ '('")
        parseWrapper(BooleanExpressionNode())?.let {
            booleanExpressionNode = it
        } ?: errorWrapper<DoNode>(message = "Произошла ошибка при парсинге ExpressionNode")
        skipThrow<DoNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}