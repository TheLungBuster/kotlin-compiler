package com.enteld.ast.nodes.scopes

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.SubjectNode
import com.enteld.ast.nodes.call.AssignVariableCall
import com.enteld.ast.nodes.call.CallNode
import com.enteld.ast.nodes.declaration.DeclarationVariableNode
import com.enteld.ast.nodes.structures.*
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ScopeFieldNode : BaseNode() {

    private var field: BaseNode? = null
    fun getField() = field

    private var returned: ReturnNode? = null
    fun getReturn() = returned

    override fun parse(): Boolean {
        when (tokenManager.peekType()) {
            Token.Type.Identifier -> {
                when (tokenManager.peekNextTokenType()) {
                    Token.Type.OperatorAssignment,
                    Token.Type.OperatorDivideAndAssign,
                    Token.Type.OperatorMultiplyAndAssign,
                    Token.Type.OperatorModAndAssign,
                    Token.Type.OperatorMinusAndAssign,
                    Token.Type.OperatorPlusAndAssign -> {
                        parseWrapper(AssignVariableCall(tokenManager.get()))?.let {
                            field = it
                        } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге AssignVariableCall")
                    }
                    else -> {
                        parseWrapper(CallNode())?.let {
                            field = it
                        } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге CallNode")
                    }
                }
            }

            Token.Type.KeywordVar,
            Token.Type.KeywordVal -> {
                val isMutable = tokenManager.peekType() == Token.Type.KeywordVar
                if (isMutable) skipThrow<ScopeFieldNode>(
                    Token.Type.KeywordVar,
                    message = "Ожидалось ключевое слово var"
                )
                else skipThrow<ScopeFieldNode>(Token.Type.KeywordVal, message = "Ожидалось ключевое слово val")
                val identifier = tokenManager.peek()
                parseWrapper(DeclarationVariableNode(identifier, isMutable))?.let {
                    field = it
                } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге DeclarationVariableNode")
            }

            Token.Type.KeywordIf -> {
                parseWrapper(IfNode())?.let {
                    field = it
                } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге IfNode")
            }

            Token.Type.KeywordDo -> {
                parseWrapper(DoNode())?.let {
                    field = it
                } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге DoNode")
            }

            Token.Type.KeywordWhile -> {
                parseWrapper(WhileNode())?.let {
                    field = it
                } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге WhileNode")
            }

            Token.Type.KeywordReturn -> {
                parseWrapper(ReturnNode())?.let {
                    returned = it
                } ?: errorWrapper<ScopeFieldNode>(message = "Произошла ошибка при парсинге ReturnNode")
            }

            else -> errorWrapper<ScopeFieldNode>(message = "Неизвестная конструкция")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}