package com.enteld.ast.nodes.scopes

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.RootSubjectNode
import com.enteld.ast.nodes.structures.ImportsNode
import com.enteld.ast.nodes.structures.PackageNode
import com.enteld.core.token.Token

class RootScopeNode: BaseNode() {

    private var packageStructure: PackageNode? = null
    fun getPackage() = packageStructure

    private var imports: ImportsNode? = null
    fun getImports() = imports

    private var subjects = mutableListOf<RootSubjectNode>()
    fun getSubjects() = subjects.toList()

    override fun parse(): Boolean {
        if (tokenManager.peekType() == Token.Type.KeywordPackage) {
            parseWrapper(PackageNode())?.let {
                packageStructure = it
            } ?: errorWrapper<RootScopeNode>(message = "Произошла ошибка при парсинге PackageNode")
        }
        if (tokenManager.peekType() == Token.Type.KeywordImport) {
            parseWrapper(ImportsNode())?.let {
                imports = it
            } ?: errorWrapper<RootScopeNode>(message = "Произошла ошибка при парсинге PackageNode")
        }
        while (tokenManager.peekType() != Token.Type.End) {
            parseWrapper(RootSubjectNode())?.let {
                subjects.add(it)
            } ?: errorWrapper<RootScopeNode>(message = "Произошла ошибка при парсинге RootSubjectNode")
        }
        return true
    }

    override fun accept() {
        TODO("Not yet implemented")
    }
}