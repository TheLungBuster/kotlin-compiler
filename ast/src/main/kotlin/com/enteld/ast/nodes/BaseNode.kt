package com.enteld.ast.nodes

import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token
import com.enteld.core.token.TokenManager
import org.koin.core.KoinComponent
import org.koin.core.inject


abstract class BaseNode() : KoinComponent {
    abstract fun parse(): Boolean
    abstract fun accept(visitor: Visitor)

    protected val tokenManager: TokenManager by inject()

    protected inline fun <reified Node : BaseNode> skipThrow(vararg type: Token.Type, message: String? = null) {
        type.forEach {
            val token = tokenManager.get()
            if (token.type != it) {
                throw Exception(
                    "Skip ${tokenManager.peek().lexeme}:${tokenManager.peek().positions} error in ${Node::class.simpleName}" +
                            " ${message?.let { "with message: $message" } ?: ""}")
            }
        }
    }

    protected inline fun <reified Node : BaseNode> skipIf(
        type: Token.Type? = null,
        condition: Boolean,
        message: String
    ): Token? {
        if (condition) {
            val token = tokenManager.get()
            type?.let {
                if (token.type == it) return token else errorWrapper<Node>(message = message)
            }
        }
        return null
    }

    protected fun <Node : BaseNode> parseWrapper(node: Node) = if (node.parse()) node else null

    protected inline fun <reified Node : BaseNode> errorWrapper(
        condition: Boolean? = null,
        message: String? = null
    ): Boolean {
        if (condition == false) {
            return true
        }
        throw Exception(
            "Error parse ${tokenManager.peek().lexeme}:${tokenManager.peek().positions} in ${Node::class.simpleName}" +
                    " ${message?.let { "with message: $it" } ?: ""}"
        )
    }
}