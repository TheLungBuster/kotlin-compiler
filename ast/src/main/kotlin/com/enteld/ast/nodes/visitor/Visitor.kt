package com.enteld.ast.nodes.visitor

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.RootSubjectNode
import com.enteld.ast.nodes.SubjectNode
import com.enteld.ast.nodes.call.*
import com.enteld.ast.nodes.declaration.*
import com.enteld.ast.nodes.expression.ArithmeticExpressionNode
import com.enteld.ast.nodes.expression.BooleanExpressionNode
import com.enteld.ast.nodes.expression.StringConcatNode
import com.enteld.ast.nodes.expression.TokenNode
import com.enteld.ast.nodes.scopes.*
import com.enteld.ast.nodes.structures.*

interface Visitor {
    fun visit(node: DoNode)
    fun visit(node: IfNode)
    fun visit(node: ImportsNode)
    fun visit(node: ImportNode)
    fun visit(node: PackageNode)
    fun visit(node: SubjectNode)
    fun visit(node: ParamNode)
    fun visit(node: ReturnNode)
    fun visit(node: WhileNode)

    fun visit(node: RootScopeNode)
    fun visit(node: RootSubjectNode)

    fun visit(node: DeclarationFuncNode)
    fun visit(node: DeclarationVariableNode)

    fun visit(node: ArrayCall)
    fun visit(node: CallNode)
    fun visit(node: FunctionCallNode)
    fun visit(node: VariableCallNode)
    fun visit(node: AssignVariableCall)
    fun visit(node: ArrayCreateCall)

    fun visit(node: ArithmeticExpressionNode)
    fun visit(node: BooleanExpressionNode)
    fun visit(node: StringConcatNode)
    fun visit(node: TokenNode)

    fun visit(node: FunctionArgumentsScopeNode)
    fun visit(node: ScopeFuncNode)
    fun visit(node: ScopeNode)
    fun visit(node: ScopeFieldNode)
}

fun List<BaseNode>.forEachVisit(visitor: Visitor) {
    this.forEach {
        it.accept(visitor)
    }
}