package com.enteld.ast.nodes

import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class RootSubjectNode : BaseNode() {

    private lateinit var subject: SubjectNode
    fun getSubject() = subject

    override fun parse(): Boolean {
        when (tokenManager.peekType()) {
            Token.Type.KeywordVar,
            Token.Type.KeywordVal,
            Token.Type.KeywordFun -> {
                parseWrapper(SubjectNode())?.let {
                    subject = it
                } ?: errorWrapper<RootSubjectNode>(message = "Ошибка при парсинге SubjectNode")
            }
            else ->  errorWrapper<RootSubjectNode>(message = "Ожидалась декларация верхнего уровня")
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}