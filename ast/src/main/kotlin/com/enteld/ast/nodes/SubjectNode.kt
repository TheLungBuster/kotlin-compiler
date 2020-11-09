package com.enteld.ast.nodes

import com.enteld.ast.nodes.declaration.DeclarationFuncNode
import com.enteld.ast.nodes.declaration.DeclarationVariableNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class SubjectNode : BaseNode() {

    enum class Declaration {
        Function,
        Variable
    }

    private lateinit var declarationType: Declaration
    fun getDeclarationType() = declarationType

    private var declaration: BaseNode? = null
    fun getDeclaration() = declaration

    private lateinit var identifier: Token

    private var isMutable: Boolean = true

    override fun parse(): Boolean {
        isMutable = tokenManager.peekType() == Token.Type.KeywordVar
        when (tokenManager.peekType()) {
            Token.Type.KeywordFun  -> {
                skipThrow<SubjectNode>(Token.Type.KeywordFun, message = "Ожидалось ключевое слово fun")
                isIdentifier()
                declarationType = Declaration.Function
                parseWrapper(DeclarationFuncNode(identifier))?.let {
                    declaration = it
                } ?: errorWrapper<SubjectNode>(message = "Произошла ошибка при парсинге DeclarationFunctionNode")
            }

            Token.Type.KeywordVal,
            Token.Type.KeywordVar   -> {
                if (isMutable) skipThrow<SubjectNode>(Token.Type.KeywordVar, message = "Ожидалось ключевое слово var")
                else skipThrow<SubjectNode>(Token.Type.KeywordVal, message = "Ожидалось ключевое слово val")
                isIdentifier()
                declarationType = Declaration.Variable
                parseWrapper(DeclarationVariableNode(identifier, isMutable))?.let {
                    declaration = it
                } ?: errorWrapper<SubjectNode>(message = "Произошла ошибка при парсинге DeclarationVariableNode")
            }
            else -> errorWrapper<SubjectNode>(message = "Недопустимая декларация")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)

    private fun isIdentifier() {
        if (tokenManager.peekType() == Token.Type.Identifier) {
            identifier = tokenManager.peek()
        } else errorWrapper<SubjectNode>(message = "При деларации ожидался Identifier")
    }
}