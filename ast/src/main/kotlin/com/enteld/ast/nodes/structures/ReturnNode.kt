package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor

class ReturnNode: BaseNode() {

    override fun parse(): Boolean {
        TODO("Not yet implemented")
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}