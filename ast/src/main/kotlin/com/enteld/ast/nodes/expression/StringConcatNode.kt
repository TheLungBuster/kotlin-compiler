package com.enteld.ast.nodes.expression

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.call.VariableCallNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class StringConcatNode : BaseNode() {

    private var strings = mutableListOf<BaseNode>()
    fun getStrings() = strings.toList()

    override fun parse(): Boolean {
        while (tokenManager.peekType() == Token.Type.LiteralString ||
            tokenManager.peekType() == Token.Type.Identifier ||
            tokenManager.peekType() == Token.Type.OperatorPlus
        ) {
            when (tokenManager.peekType()) {
                Token.Type.LiteralString -> {
                    parseWrapper(TokenNode())?.let {
                        strings.add(it)
                    }?: errorWrapper<StringConcatNode>(message = "Произошла ошибка при парсинге TokenNode")
                }

                Token.Type.Identifier -> {
                    parseWrapper(VariableCallNode(tokenManager.get()))?.let {
                        strings.add(it)
                    }?: errorWrapper<StringConcatNode>(message = "Произошла ошибка при парсинге TokenNode")
                }
                else -> errorWrapper<StringConcatNode>(message = "Ожидалось Literal или Identifier")
            }
            if (tokenManager.peekType() == Token.Type.OperatorPlus) {
                parseWrapper(TokenNode())?.let {
                    strings.add(it)
                }?: errorWrapper<StringConcatNode>(message = "Произошла ошибка при парсинге TokenNode")
            } else {
                return true
            }
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}