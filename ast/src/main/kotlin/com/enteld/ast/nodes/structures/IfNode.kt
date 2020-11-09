package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.expression.BooleanExpressionNode
import com.enteld.ast.nodes.scopes.ScopeNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class IfNode: BaseNode() {

    private var booleanExpressionNode = mutableListOf<BooleanExpressionNode>()
    fun getExpressionNode() = booleanExpressionNode.toList()

    private var scope = mutableListOf<ScopeNode>()
    fun getScope() = scope.toList()

    override fun parse(): Boolean {
        while (tokenManager.peekType() == Token.Type.KeywordIf) {
            skipThrow<IfNode>(Token.Type.KeywordIf, Token.Type.LParen, message = "Ожидался символ '('")
            parseWrapper(BooleanExpressionNode())?.let {
                booleanExpressionNode.add(it)
            } ?: errorWrapper<IfNode>(message = "Произошла ошибка при парсинге ExpressionNode")
            skipThrow<IfNode>(Token.Type.RParen, message = "Ожидался символ ')'")
            parseWrapper(ScopeNode())?.let {
                scope.add(it)
            } ?: errorWrapper<IfNode>(message = "Произошла ошибка при парсинге ScopeFieldNode")
            if (tokenManager.peekType() == Token.Type.KeywordElse &&
                tokenManager.peekNextTokenType() == Token.Type.KeywordIf
            ) {
                skipThrow<IfNode>(Token.Type.KeywordElse, Token.Type.LParen, message = "Ожидался else")
            } else continue
        }

        if (tokenManager.peekType() == Token.Type.KeywordElse) {
            skipThrow<IfNode>(Token.Type.KeywordElse, Token.Type.LParen, message = "Ожидался else")
            parseWrapper(BooleanExpressionNode())?.let {
                booleanExpressionNode.add(it)
            } ?: errorWrapper<IfNode>(message = "Произошла ошибка при парсинге ExpressionNode")
            skipThrow<IfNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}