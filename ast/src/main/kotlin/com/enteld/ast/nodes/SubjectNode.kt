package com.enteld.ast.nodes

import com.enteld.core.token.Token

class SubjectNode: BaseNode() {

    private var declaration: BaseNode? = null
    fun getDeclaration() = declaration

    private var isMutable: Boolean = true
    fun getMutable() = isMutable

    override fun parse(): Boolean {
        TODO("Not yet implemented")
    }

    override fun accept() {
        TODO("Not yet implemented")
    }
}