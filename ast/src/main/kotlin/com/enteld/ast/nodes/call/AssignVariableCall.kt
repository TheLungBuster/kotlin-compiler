package com.enteld.ast.nodes.call

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.expression.ArithmeticExpressionNode
import com.enteld.ast.nodes.expression.StringConcatNode
import com.enteld.ast.nodes.interfaces.HasIdentifier
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class AssignVariableCall(
    override var identifier: Token
): BaseNode(), HasIdentifier {

    private lateinit var assignation: Token
    fun getAssignation() = assignation

    private lateinit var scope: BaseNode
    fun getScope() = scope

    override fun parse(): Boolean {
        when (tokenManager.peekType()) {
            Token.Type.OperatorAssignment,
            Token.Type.OperatorDivideAndAssign,
            Token.Type.OperatorMultiplyAndAssign,
            Token.Type.OperatorModAndAssign,
            Token.Type.OperatorMinusAndAssign,
            Token.Type.OperatorPlusAndAssign -> {
                assignation = tokenManager.get()
            }
            else -> errorWrapper<AssignVariableCall>(message = "Ожидалось Assignation")
        }

        when (tokenManager.peekType()) {
            Token.Type.LiteralInt,
            Token.Type.LiteralDouble,
            Token.Type.Identifier,
            Token.Type.LParen -> {
                parseWrapper(ArithmeticExpressionNode())?.let {
                    scope = it
                }
            }
            Token.Type.LiteralString -> {
                parseWrapper(StringConcatNode())?.let {
                    scope = it
                }
            }
            else -> errorWrapper<AssignVariableCall>(message = "Ожидалось Literal или Identifier")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}