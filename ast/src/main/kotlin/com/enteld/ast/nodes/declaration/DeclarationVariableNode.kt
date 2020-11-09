package com.enteld.ast.nodes.declaration

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.call.ArrayCreateCall
import com.enteld.ast.nodes.call.CallNode
import com.enteld.ast.nodes.expression.ArithmeticExpressionNode
import com.enteld.ast.nodes.expression.TokenNode
import com.enteld.ast.nodes.interfaces.HasIdentifier
import com.enteld.ast.nodes.interfaces.IsMutable
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class DeclarationVariableNode(
    override var identifier: Token,
    override val isMutable: Boolean
) : BaseNode(), HasIdentifier, IsMutable {

    private var variableType: Token? = null
    fun getVariableType() = variableType

    private var variable: BaseNode? = null
    fun getVariable() = variable

    private var scope: BaseNode? = null
    fun getScope() = scope

    private var isList = false
    fun getIsList() = isList


    override fun parse(): Boolean {
        skipThrow<DeclarationVariableNode>(Token.Type.Identifier, message = "Ожидался identifier")
        if (tokenManager.peekType() == Token.Type.Colon) {
            skipThrow<DeclarationVariableNode>(Token.Type.Colon, message = "Ожидался символ ':'")
            when (tokenManager.peekType()) {
                Token.Type.TypeInt,
                Token.Type.TypeDouble,
                Token.Type.TypeString,
                Token.Type.TypeBoolean,
                Token.Type.TypeAny -> variableType = tokenManager.get()
                Token.Type.TypeList -> {
                    skipThrow<DeclarationVariableNode>(
                        Token.Type.TypeList,
                        Token.Type.OperatorLessThen,
                        message = "Ожидалось '<'"
                    )
                    when (tokenManager.peekType()) {
                        Token.Type.TypeInt,
                        Token.Type.TypeDouble,
                        Token.Type.TypeString,
                        Token.Type.TypeBoolean,
                        Token.Type.TypeAny -> {
                            isList = true
                            variableType = tokenManager.get()
                        }
                        else -> errorWrapper<DeclarationVariableNode>(message = "Ожидалось объявление типа массива")
                    }
                    skipThrow<DeclarationVariableNode>(Token.Type.OperatorGreaterThen)
                }
                else -> errorWrapper<DeclarationVariableNode>(message = "Ожидалось объявление типа переменной")
            }
        }
        skipThrow<DeclarationVariableNode>(
            Token.Type.OperatorAssignment,
            message = "Переменная должна быть инициализирована"
        )
        when (tokenManager.peekType()) {
            Token.Type.LiteralInt,
            Token.Type.TypeDouble,
            Token.Type.Identifier-> {
                parseWrapper(ArithmeticExpressionNode())?.let {
                    variable = it
                }
                    ?: errorWrapper<DeclarationVariableNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
            }
            Token.Type.LiteralString,
            Token.Type.LiteralTrue,
            Token.Type.LiteralFalse -> {
                parseWrapper(TokenNode())?.let {
                    variable = it
                }
                    ?: errorWrapper<DeclarationVariableNode>(message = "Произошла ошибка при парсинге TokenNode")
            }

            Token.Type.TypeList -> {
                parseWrapper(ArrayCreateCall())?.let {
                    scope = it
                } ?: errorWrapper<DeclarationVariableNode>(message = "Произошла ошибка при парсинге CallNode")
            }
            else -> errorWrapper<DeclarationVariableNode>(message = "Не инициализированная переменная")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}