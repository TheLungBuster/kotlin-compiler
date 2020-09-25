package com.enteld.ast.nodes.structures

import com.enteld.ast.nodes.BaseNode
import com.enteld.core.token.Token

class ImportNode: BaseNode() {

    private var library = mutableListOf<Token>()
    fun getLibrary() = library

    override fun parse(): Boolean {
        skipThrow<PackageNode>(Token.Type.KeywordPackage, message = "Ожидалось ключевое слово package")
        do {
            if (tokenManager.peekType() == Token.Type.Identifier) {
                library.add(tokenManager.get())
                skipThrow<PackageNode>(Token.Type.Dot, message = "Ожидался символ '.'")
                library.add(tokenManager.get())
            } else {
                errorWrapper<PackageNode>(
                    tokenManager.peekType() == Token.Type.Dot,
                    "Ожидался Identifier или '.'"
                )
            }
        } while( tokenManager.peekType() == Token.Type.Identifier || tokenManager.peekType() == Token.Type.Dot )
        return true
    }

    override fun accept() {
        TODO("Not yet implemented")
    }
}