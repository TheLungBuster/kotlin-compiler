package com.enteld.ast.nodes.expression

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.call.CallNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class ArithmeticExpressionNode(
    val identifier: CallNode? = null,
    var priority: Priority = Priority.Second,
    var isInParen: Boolean = false
) : BaseNode() {

    enum class Priority {
        First,
        Second
    }

    private var nodes = mutableListOf<BaseNode>()
    fun getNodes() = nodes.toList()

    private var identifierBuffer: CallNode? = null

    override fun parse(): Boolean {
        if (identifier != null) {
            nodes.add(identifier)
        }
        while (tokenManager.peekType() == Token.Type.LiteralInt ||
            tokenManager.peekType() == Token.Type.LiteralDouble ||
            tokenManager.peekType() == Token.Type.LiteralInt ||
            tokenManager.peekType() == Token.Type.LParen ||
            tokenManager.peekType() == Token.Type.RParen ||
            tokenManager.peekType() == Token.Type.OperatorPlus ||
            tokenManager.peekType() == Token.Type.OperatorMinus ||
            tokenManager.peekType() == Token.Type.OperatorMultiply ||
            tokenManager.peekType() == Token.Type.OperatorDivision ||
            tokenManager.peekType() == Token.Type.OperatorMod ||
            tokenManager.peekType() == Token.Type.Identifier
        ) {
            when (tokenManager.peekType()) {
                Token.Type.LParen -> {
                    skipThrow<ArithmeticExpressionNode>(Token.Type.LParen, message = "Ожидался символ '('")
                    parseWrapper(ArithmeticExpressionNode(isInParen = true))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
                }

                Token.Type.RParen -> {
                    if (isInParen) {
                        skipThrow<ArithmeticExpressionNode>(Token.Type.RParen, message = "Ожидался символ ')'")
                        return true
                    }
                    return true
                }

                Token.Type.LiteralInt,
                Token.Type.LiteralDouble -> {
                    if (priority == Priority.Second) {
                        if (tokenManager.peekNextTokenType() == Token.Type.OperatorMultiply ||
                            tokenManager.peekNextTokenType() == Token.Type.OperatorDivision ||
                            tokenManager.peekNextTokenType() == Token.Type.OperatorMod
                        ) {
                            parseWrapper(
                                ArithmeticExpressionNode(
                                    priority = Priority.First
                                )
                            )?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
                        } else if (
                            tokenManager.peekNextTokenType() == Token.Type.OperatorPlus ||
                            tokenManager.peekNextTokenType() == Token.Type.OperatorMinus
                        ) {
                            parseWrapper(TokenNode())?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                        } else {
                            parseWrapper(TokenNode())?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                            return true
                        }
                    } else {
                        if (tokenManager.peekNextTokenType() == Token.Type.OperatorMultiply ||
                            tokenManager.peekNextTokenType() == Token.Type.OperatorDivision ||
                            tokenManager.peekNextTokenType() == Token.Type.OperatorMod
                        ) {
                            parseWrapper(TokenNode())?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                        } else {
                            parseWrapper(TokenNode())?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                            return true
                        }
                    }
                }

                Token.Type.Identifier -> {
                    if (tokenManager.peekType() == Token.Type.Identifier) {
                        parseWrapper(CallNode())?.let {
                            identifierBuffer = it
                        } ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге CallNode")
                    }
                    if (priority == Priority.Second) {
                        if (tokenManager.peekType() == Token.Type.OperatorMultiply ||
                            tokenManager.peekType() == Token.Type.OperatorDivision ||
                            tokenManager.peekType() == Token.Type.OperatorMod
                        ) {
                            parseWrapper(
                                ArithmeticExpressionNode(
                                    priority = Priority.First,
                                    identifier = identifierBuffer
                                )
                            )?.let {
                                nodes.add(it)
                            }
                                ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
                        } else if (
                            tokenManager.peekType() == Token.Type.OperatorPlus ||
                            tokenManager.peekType() == Token.Type.OperatorMinus
                        ) {
                            nodes.add(identifierBuffer!!)

                        } else {
                            nodes.add(identifierBuffer!!)
                            return true
                        }
                    } else {
                        if (tokenManager.peekType() == Token.Type.OperatorMultiply ||
                            tokenManager.peekType() == Token.Type.OperatorDivision ||
                            tokenManager.peekType() == Token.Type.OperatorMod
                        ) {
                            nodes.add(identifierBuffer!!)
                        } else {
                            nodes.add(identifierBuffer!!)
                            return true
                        }
                    }
                }

                Token.Type.OperatorPlus,
                Token.Type.OperatorMinus -> {
                    if (priority == Priority.Second) {
                        parseWrapper(TokenNode())?.let {
                            nodes.add(it)
                        } ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                    } else {
                        return true
                    }
                }

                Token.Type.OperatorMultiply,
                Token.Type.OperatorDivision,
                Token.Type.OperatorMod -> {
                    parseWrapper(TokenNode())?.let {
                        nodes.add(it)
                    } ?: errorWrapper<ArithmeticExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                }

                else -> return true
            }
            identifierBuffer = null
        }
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}