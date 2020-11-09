package com.enteld.ast.nodes.declaration

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.interfaces.HasIdentifier
import com.enteld.ast.nodes.scopes.ScopeFuncNode
import com.enteld.ast.nodes.structures.ParamNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class DeclarationFuncNode(
    override var identifier: Token
) : BaseNode(), HasIdentifier {

    lateinit var scope: BaseNode

    private val params = mutableListOf<ParamNode>()
    fun getParams() = params.toList()

    private var returnType: Token.Type = Token.Type.TypeUnit
    fun getReturnType() = returnType

    override fun parse(): Boolean {
        skipThrow<DeclarationFuncNode>(Token.Type.Identifier, message = "Ожидался identifier")
        skipThrow<DeclarationFuncNode>(Token.Type.LParen, message = "Ожидался символ '('")
        while (tokenManager.peekType() != Token.Type.RParen) {
            if (tokenManager.peekType() == Token.Type.Identifier) {
                parseWrapper(ParamNode())?.let {
                    params.add(it)
                } ?: errorWrapper<DeclarationFuncNode>(message = "Произошла ошибка при парсинге ParamNode")
            } else errorWrapper<DeclarationFuncNode>(message = "Ожидался Identifier")
            skipIf<DeclarationFuncNode>(
                condition = tokenManager.peekType() == Token.Type.Comma,
                message = "Ожидался символ ','"
            )
        }
        skipThrow<DeclarationFuncNode>(Token.Type.RParen, message = "Ожидался символ ')'")
        if (tokenManager.peekType() == Token.Type.Colon) {
            when (tokenManager.get().type) {
                Token.Type.TypeInt,
                Token.Type.TypeDouble,
                Token.Type.TypeString,
                Token.Type.TypeBoolean,
                Token.Type.TypeAny,
                Token.Type.Identifier -> returnType = tokenManager.get().type
                else -> errorWrapper<DeclarationFuncNode>(message = "Ожидался возвращаемый тип функции")
            }
        }

        when (tokenManager.peekType()) {
            Token.Type.LFigured -> {
                parseWrapper(ScopeFuncNode(params.toList(), returnType))?.let {
                    scope = it
                } ?: errorWrapper<DeclarationFuncNode>(message = "Произошла ошибка при парсинге ScopeFuncNode")
            }
            else -> errorWrapper<DeclarationFuncNode>(message = "Ожидался символ '{' или '=")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}