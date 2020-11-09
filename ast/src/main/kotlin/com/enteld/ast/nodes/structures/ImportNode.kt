package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ImportNode: BaseNode() {

    private var library = mutableListOf<Token>()
    fun getLibrary() = library

    fun libraryString(): String {
        var string = ""
        library.forEach {
            string += it.lexeme + " "
        }
        return string
    }

    override fun parse(): Boolean {
        skipThrow<ImportNode>(Token.Type.KeywordImport, message = "Ожидалось ключевое слово Import")
        while(tokenManager.peekType() == Token.Type.Identifier ||
            tokenManager.peekType() == Token.Type.OperatorMultiply ) {
            if (
                tokenManager.peekType() == Token.Type.Identifier ||
                tokenManager.peekType() == Token.Type.OperatorMultiply
            ) {
                library.add(tokenManager.get())
                if (tokenManager.peekType() == Token.Type.Dot) {
                    skipThrow<ImportNode>(Token.Type.Dot, message = "Ожидался символ '.'")
                } else continue
            } else continue
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}