package com.enteld.ast.nodes

import com.enteld.core.token.Token

class RootSubjectNode : BaseNode() {

    private lateinit var subject: SubjectNode
    fun getSubject() = subject

    override fun parse(): Boolean {
        when (tokenManager.peekType()) {
            Token.Type.KeywordClass, Token.Type.KeywordVar,
            Token.Type.KeywordVal, Token.Type.KeywordFun -> {
                parseWrapper(SubjectNode())?.let {

                } ?: errorWrapper<RootSubjectNode>(message = "Ошибка при парсинге SubjectNode")
            }
            else ->  errorWrapper<RootSubjectNode>(message = "Ожидалась декларация верхнего уровня")
        }
        return true
    }

    override fun accept() {
        TODO("Not yet implemented")
    }
}