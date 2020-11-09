package com.enteld.ast.nodes.scopes

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.call.CallNode
import com.enteld.ast.nodes.call.VariableCallNode
import com.enteld.ast.nodes.declaration.DeclarationFuncNode
import com.enteld.ast.nodes.expression.ArithmeticExpressionNode
import com.enteld.ast.nodes.expression.TokenNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class FunctionArgumentsScopeNode : BaseNode() {

    private var arguments = mutableListOf<BaseNode>()
    fun getArguments() = arguments

    override fun parse(): Boolean {
        while (tokenManager.peekType() != Token.Type.RParen) {
            when (tokenManager.peekType()) {
                Token.Type.Identifier -> {
                    var identifier: CallNode? = null
                    parseWrapper(CallNode())?.let {
                        identifier = it
                    } ?: errorWrapper<FunctionArgumentsScopeNode>(message = "Произошла ошибка при парсинге ScopeNode")
                    when (tokenManager.peekNextTokenType()) {
                        Token.Type.OperatorPlus,
                        Token.Type.OperatorMinus,
                        Token.Type.OperatorMultiply,
                        Token.Type.OperatorDivision,
                        Token.Type.OperatorMod -> {
                            parseWrapper(ArithmeticExpressionNode(identifier = identifier))?.let {
                                arguments.add(it)
                            } ?: errorWrapper<FunctionArgumentsScopeNode>(message = "Произошла ошибка при парсинге FunctionArgumentsScopeNode")
                        }
                        else -> arguments.add(identifier!!)
                    }
                }

                Token.Type.LiteralTrue,
                Token.Type.LiteralFalse -> {
                    parseWrapper(TokenNode())?.let {
                        arguments.add(it)
                    }
                }

                Token.Type.LiteralInt,
                Token.Type.LiteralDouble -> {
                    parseWrapper(ArithmeticExpressionNode())?.let {
                        arguments.add(it)
                    } ?: errorWrapper<FunctionArgumentsScopeNode>(message = "Произошла ошибка при парсинге FunctionArgumentsScopeNode")
                }

                else -> errorWrapper<DeclarationFuncNode>(message = "Ожидался аргумент")
            }
            if (tokenManager.peekType() == Token.Type.Comma) {
                skipThrow<FunctionArgumentsScopeNode>(Token.Type.Comma)
            }
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}