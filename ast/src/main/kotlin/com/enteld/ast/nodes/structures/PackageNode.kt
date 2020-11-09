package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class PackageNode: BaseNode() {

    private var packagePath = mutableListOf<Token>()
    fun getPackagePath() = packagePath

    fun packageString(): String {
        var string = ""
        packagePath.map {
            string += it.lexeme + " "
        }
        return string
    }

    override fun parse(): Boolean {
        skipThrow<PackageNode>(Token.Type.KeywordPackage, message = "Ожидалось ключевое слово package")
        do {
            if (tokenManager.peekType() == Token.Type.Identifier) {
                packagePath.add(tokenManager.get())
                skipThrow<PackageNode>(Token.Type.Dot, message = "Ожидался символ '.'")
                packagePath.add(tokenManager.get())
            } else {
                errorWrapper<PackageNode>(
                    tokenManager.peekType() == Token.Type.Dot,
                    "Ожидался Identifier или '*'"
                )
            }
        } while( tokenManager.peekType() == Token.Type.Identifier || tokenManager.peekType() == Token.Type.Dot )
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}