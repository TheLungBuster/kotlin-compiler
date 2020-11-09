package com.enteld.ast.nodes

import com.enteld.ast.nodes.call.*
import com.enteld.ast.nodes.declaration.DeclarationFuncNode
import com.enteld.ast.nodes.declaration.DeclarationVariableNode
import com.enteld.ast.nodes.expression.ArithmeticExpressionNode
import com.enteld.ast.nodes.expression.BooleanExpressionNode
import com.enteld.ast.nodes.expression.StringConcatNode
import com.enteld.ast.nodes.expression.TokenNode
import com.enteld.ast.nodes.scopes.*
import com.enteld.ast.nodes.structures.*
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.ast.nodes.visitor.forEachVisit
import com.github.ajalt.mordant.AnsiCode
import com.github.ajalt.mordant.TermColors
import org.koin.core.KoinComponent
import org.koin.core.inject

class XMLVisitorImpl : Visitor, KoinComponent {

    private var nesting = 0L

    private val tabs: () -> String = {
        var string = ""
        for (i in 0 until nesting) string += "  "
        string
    }

    private val term by inject<TermColors>()

    var xmlTreeColored = ""
        private set

    fun xmlTreeColored() = print(xmlTreeColored)

    private inline fun <reified Node : BaseNode> xmlPatternBody(
        noinline vars: (() -> Unit)? = null,
        noinline block: (() -> Unit)? = null,
        color: AnsiCode = term.reset
    ) {
        val name = Node::class.simpleName?.removeSuffix("Node")
        "${tabs()}<$name".also {
            xmlTreeColored += color(it)
        }
        vars?.invoke()
        block?.let {
            ">".also {
                xmlTreeColored += "${color(it)}\n"
            }
            nesting++
            it()
            nesting--
            "${tabs()}<\\$name>".also {
                xmlTreeColored += "${color(it)}\n"
            }
        } ?: ">".also {
            xmlTreeColored += "${color(it)}\n"
        }

    }

    private fun xmlPatternVar(color: AnsiCode = term.reset, name: String, value: Any?) {
        value?.let {
            xmlTreeColored += " $name=\"${color(it.toString())}\""
        }
    }

    override fun visit(node: DoNode) {
        xmlPatternBody<DoNode>(
            color = term.blue,
            block = {
                node.getExpressionNode().accept(this)
                node.getScope().accept(this)
            }
        )
    }

    override fun visit(node: IfNode) {
        xmlPatternBody<IfNode>(
            color = term.blue,
            block = {
                node.getExpressionNode()?.forEachVisit(this)
                node.getScope()?.forEachVisit(this)
            }
        )
    }

    override fun visit(node: ImportsNode) {
        xmlPatternBody<ImportsNode>(
            color = term.yellow,
            block = {
                node.getImports().forEachVisit(this)
            }
        )
    }

    override fun visit(node: ImportNode) {
        xmlPatternBody<ImportNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "lib",
                    value = node.libraryString()
                )
            }
        )
    }

    override fun visit(node: PackageNode) {
        xmlPatternBody<PackageNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "package",
                    value = node.packageString()
                )
            }
        )
    }

    override fun visit(node: SubjectNode) {
        xmlPatternBody<SubjectNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "declarationType",
                    value = node.getDeclarationType().name
                )
            },
            block = {
                if (node.getDeclaration() != null) {
                    node.getDeclaration()?.accept(this)
                }
            }
        )
    }

    override fun visit(node: ParamNode) {
        xmlPatternBody<ParamNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "identifier",
                    value = node.getIdentifier().lexeme
                )
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "paramType",
                    value = node.getParamType().lexeme
                )
            }
        )
    }

    override fun visit(node: ReturnNode) {
        TODO("Not yet implemented")
    }

    override fun visit(node: WhileNode) {
        xmlPatternBody<WhileNode>(
            color = term.blue,
            block = {
                node.getExpressionNode().accept(this)
                node.getScope().accept(this)
            }
        )
    }

    override fun visit(node: RootScopeNode) {
        xmlPatternBody<RootScopeNode>(
            color = term.brightWhite,
            block = {
                node.getPackage()?.accept(this)
                node.getImports()?.accept(this)
                node.getSubjects().forEachVisit(this)
            }
        )
    }

    override fun visit(node: RootSubjectNode) {
        xmlPatternBody<RootSubjectNode>(
            block = {
                node.getSubject().accept(this)
            }
        )
    }

    override fun visit(node: DeclarationFuncNode) {
        xmlPatternBody<DeclarationFuncNode>(
            color = term.cyan,
            vars = {
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "identifier",
                    value = node.identifier.lexeme
                )
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "returnType",
                    value = node.getReturnType()
                )
            },
            block = {
                node.getParams().forEachVisit(this)
                node.scope.accept(this)
            }
        )
    }

    override fun visit(node: DeclarationVariableNode) {
        xmlPatternBody<DeclarationVariableNode>(
            color = term.yellow,
            vars = {
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "mutable",
                    value = node.isMutable
                )
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "identifier",
                    value = node.identifier.lexeme
                )
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "List",
                    value = node.getIsList()
                )
            },
            block = {
                node.getVariable()?.accept(this)
                if (node.getIsList()) {
                    node.getScope()?.accept(this)
                }
            }
        )
    }

    override fun visit(node: ArrayCall) {
        xmlPatternBody<ArrayCall>(
            color = term.brightGreen,
            block = {
                node.getCall()
                node.getId()
            }
        )
    }

    override fun visit(node: CallNode) {
        xmlPatternBody<CallNode>(
            color = term.cyan,
            vars = {
                xmlPatternVar(
                    color = term.cyan,
                    name = "identifier",
                    value = node.getIdentifier().lexeme
                )
            },
            block = {
                if (node.getCallSubject() != null) {
                    node.getCallSubject()?.accept(this)
                }
            }
        )
    }

    override fun visit(node: FunctionCallNode) {
        xmlPatternBody<FunctionCallNode>(
            color = term.yellow,
            vars = {
                xmlPatternVar(
                    color = term.yellow,
                    name = "identifier",
                    value = node.identifier.lexeme
                )
            },
            block = {
                if (node.getArguments() != null) {
                    node.getArguments()?.accept(this)
                }
                if (node.getScope() != null) {
                    node.getScope()?.accept(this)
                }
            }
        )
    }

    override fun visit(node: VariableCallNode) {
        xmlPatternBody<VariableCallNode>(
            color = term.brightYellow,
            vars = {
                xmlPatternVar(
                    color = term.brightYellow,
                    name = "identifier",
                    value = node.identifier.lexeme
                )
            },
            block = {
                if (node.getCallSubject() != null) {
                    node.getCallSubject()?.accept(this)
                }
            }
        )
    }

    override fun visit(node: AssignVariableCall) {
        xmlPatternBody<AssignVariableCall>(
            color = term.brightMagenta,
            vars = {
                xmlPatternVar(
                    color = term.brightMagenta,
                    name = "identifier",
                    value = node.identifier.lexeme
                )
                xmlPatternVar(
                    color = term.brightMagenta,
                    name = "assignation",
                    value = node.getAssignation().lexeme
                )
            },
            block = {
                node.getScope().accept(this)
            }
        )
    }

    override fun visit(node: ArrayCreateCall) {
        xmlPatternBody<ArrayCreateCall>(
            color = term.brightGreen,
            block = {
                node.getCallSubject()
            }
        )
    }

    override fun visit(node: ArithmeticExpressionNode) {
        xmlPatternBody<ArithmeticExpressionNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "priority",
                    value = node.priority.name
                )
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "InParen",
                    value = node.isInParen.toString()
                )
            },
            block = {
                node.getNodes().forEachVisit(this)
            }
        )
    }

    override fun visit(node: BooleanExpressionNode) {
        xmlPatternBody<BooleanExpressionNode>(
            vars = {
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "priority",
                    value = node.priority.name
                )
                xmlPatternVar(
                    color = term.brightCyan,
                    name = "InParen",
                    value = node.isInParen.toString()
                )
            },
            block = {
                node.getNodes().forEachVisit(this)
            }
        )
    }

    override fun visit(node: StringConcatNode) {
        xmlPatternBody<StringConcatNode>(
            block = {
                node.getStrings().forEachVisit(this)
            }
        )
    }

    override fun visit(node: TokenNode) {
        xmlPatternBody<TokenNode>(
            vars = {
                xmlPatternVar(
                    color = term.white,
                    name = "token",
                    value = node.getToken().lexeme
                )
            }
        )
    }

    override fun visit(node: FunctionArgumentsScopeNode) {
        xmlPatternBody<FunctionArgumentsScopeNode>(
            color = term.blue,
            block = {
                node.getArguments().forEachVisit(this)
            }
        )
    }

    override fun visit(node: ScopeFuncNode) {
        xmlPatternBody<ScopeFuncNode>(
            color = term.cyan,
            block = {
                node.getFields().forEachVisit(this)
            }
        )
    }

    override fun visit(node: ScopeNode) {
        xmlPatternBody<ScopeNode>(
            color = term.bold,
            block = {
                node.getFields().forEachVisit(this)
            }
        )
    }

    override fun visit(node: ScopeFieldNode) {
        xmlPatternBody<ScopeFieldNode>(
            color = term.white,
            block = {
                if (node.getField() != null) {
                    node.getField()?.accept(this)
                }
                if (node.getReturn() != null) {
                    node.getReturn()?.accept(this)
                }
            }
        )
    }
}