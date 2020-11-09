package com.enteld.ast.nodes.expression

import com.enteld.ast.nodes.BaseNode
import com.enteld.ast.nodes.call.CallNode
import com.enteld.ast.nodes.visitor.Visitor
import com.enteld.core.token.Token

class BooleanExpressionNode(
    val identifier: CallNode? = null,
    var priority: Priority = Priority.Four,
    var isInParen: Boolean = false
) : BaseNode() {

    enum class Priority {
        First,
        Second,
        Third,
        Four
    }

    private var nodes = mutableListOf<BaseNode>()
    fun getNodes() = nodes.toList()

    private var identifierBuffer: CallNode? = null

    override fun parse(): Boolean {
        if (identifier != null) {
            nodes.add(identifier)
        }
        while (tokenManager.peekType() == Token.Type.LiteralFalse ||
            tokenManager.peekType() == Token.Type.LiteralTrue ||
            tokenManager.peekType() == Token.Type.OperatorLessThen ||
            tokenManager.peekType() == Token.Type.OperatorLessThenOrEqual ||
            tokenManager.peekType() == Token.Type.OperatorGreaterThen ||
            tokenManager.peekType() == Token.Type.OperatorGreaterThanOrEqual ||
            tokenManager.peekType() == Token.Type.OperatorAnd ||
            tokenManager.peekType() == Token.Type.OperatorOr ||
            tokenManager.peekType() == Token.Type.OperatorEqual ||
            tokenManager.peekType() == Token.Type.OperatorNotEqual ||
            tokenManager.peekType() == Token.Type.OperatorNot ||
            tokenManager.peekType() == Token.Type.LParen ||
            tokenManager.peekType() == Token.Type.RParen ||
            tokenManager.peekType() == Token.Type.LiteralInt ||
            tokenManager.peekType() == Token.Type.LiteralDouble ||
            tokenManager.peekType() == Token.Type.LParen ||
            tokenManager.peekType() == Token.Type.RParen ||
            tokenManager.peekType() == Token.Type.Identifier
        ) {
            when (tokenManager.peekType()) {

                Token.Type.LParen -> {
                    skipThrow<BooleanExpressionNode>(Token.Type.LParen, message = "Ожидался символ '('")
                    parseWrapper(BooleanExpressionNode(isInParen = true))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                }

                Token.Type.RParen -> {
                    if (isInParen) {
                        skipThrow<BooleanExpressionNode>(Token.Type.RParen, message = "Ожидался символ ')'")
                        return true
                    }
                    return true
                }

                Token.Type.LiteralInt,
                Token.Type.LiteralDouble -> {
                    if (tokenManager.peekNextTokenType() == Token.Type.OperatorPlus ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMinus ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMultiply ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorDivision ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMod
                    ) {
                        parseWrapper(ArithmeticExpressionNode())?.let {
                            nodes.add(it)
                        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
                    } else {
                        if (parseOperand()) {
                            return true
                        }
                    }
                }

                Token.Type.Identifier -> {
                    parseWrapper(CallNode())?.let {
                        identifierBuffer = it
                    } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге CallNode")

                    if (tokenManager.peekNextTokenType() == Token.Type.OperatorPlus ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMinus ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMultiply ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorDivision ||
                        tokenManager.peekNextTokenType() == Token.Type.OperatorMod
                    ) {
                        parseWrapper(ArithmeticExpressionNode(identifier = identifierBuffer))?.let {
                            nodes.add(it)
                        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге ArithmeticExpressionNode")
                    } else {
                        if (parseOperand(identifier = identifierBuffer!!)) {
                            return true
                        }
                    }
                }

                Token.Type.LiteralTrue,
                Token.Type.LiteralFalse -> {
                    if (parseOperand()) {
                        return true
                    }
                }

                Token.Type.OperatorNot -> {
                    parseWrapper(BooleanExpressionNode(priority = Priority.First))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                }

                Token.Type.OperatorLessThenOrEqual,
                Token.Type.OperatorLessThen,
                Token.Type.OperatorGreaterThanOrEqual,
                Token.Type.OperatorGreaterThen -> {
                    when (priority) {
                        Priority.First -> return true
                        else -> parseWrapper(TokenNode())?.let {
                            nodes.add(it)
                        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                    }
                }

                Token.Type.OperatorEqual,
                Token.Type.OperatorNotEqual -> {
                    when (priority) {
                        Priority.First,
                        Priority.Second -> return true
                        else -> parseWrapper(TokenNode())?.let {
                            nodes.add(it)
                        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                    }
                }

                Token.Type.OperatorOr,
                Token.Type.OperatorAnd -> {
                    when (priority) {
                        Priority.First,
                        Priority.Second,
                        Priority.Third -> return true
                        else -> parseWrapper(TokenNode())?.let {
                            nodes.add(it)
                        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                    }
                }

                else -> return true
            }
            identifierBuffer = null
        }
        return true
    }

    private fun parseOperand(): Boolean {
        when (priority) {
            Priority.First -> {
                return getAndReturn()
            }

            Priority.Second -> {
                if (tokenManager.peekNextTokenType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThen
                ) {
                    parseWrapper(TokenNode())?.let {
                        nodes.add(it)
                    } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                } else {
                    return getAndReturn()
                }
            }

            Priority.Third -> {
                if (tokenManager.peekNextTokenType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThen
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Second))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else if (tokenManager.peekNextTokenType() == Token.Type.OperatorEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorNotEqual
                ) {
                    parseWrapper(TokenNode())?.let {
                        nodes.add(it)
                    } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                } else {
                    return getAndReturn()
                }
            }

            Priority.Four -> {
                if (tokenManager.peekNextTokenType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorGreaterThen
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Second))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else if (tokenManager.peekNextTokenType() == Token.Type.OperatorEqual ||
                    tokenManager.peekNextTokenType() == Token.Type.OperatorNotEqual
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Third))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else {
                    parseWrapper(TokenNode())?.let {
                        nodes.add(it)
                    } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
                }
            }
        }
        return false
    }

    private fun parseOperand(identifier: CallNode): Boolean {
        when (priority) {
            Priority.First -> {
                return getAndReturn(identifier)
            }

            Priority.Second -> {
                if (tokenManager.peekType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThen
                ) {
                    nodes.add(identifier)
                } else {
                    return getAndReturn(identifier)
                }
            }

            Priority.Third -> {
                if (tokenManager.peekType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThen
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Second, identifier = identifier))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else if (tokenManager.peekType() == Token.Type.OperatorEqual ||
                    tokenManager.peekType() == Token.Type.OperatorNotEqual
                ) {
                    nodes.add(identifier)
                } else {
                    return getAndReturn(identifier)
                }
            }

            Priority.Four -> {
                if (tokenManager.peekType() == Token.Type.OperatorLessThenOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorLessThen ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThanOrEqual ||
                    tokenManager.peekType() == Token.Type.OperatorGreaterThen
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Second, identifier = identifier))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else if (tokenManager.peekType() == Token.Type.OperatorEqual ||
                    tokenManager.peekType() == Token.Type.OperatorNotEqual
                ) {
                    parseWrapper(BooleanExpressionNode(priority = Priority.Third, identifier = identifier))?.let {
                        nodes.add(it)
                    }
                        ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге BooleanExpressionNode")
                } else {
                    nodes.add(identifier)
                }
            }
        }
        return false
    }

    private fun getAndReturn(): Boolean {
        parseWrapper(TokenNode())?.let {
            nodes.add(it)
        } ?: errorWrapper<BooleanExpressionNode>(message = "Произошла ошибка при парсинге TokenNode")
        return true
    }

    private fun getAndReturn(identifier: CallNode): Boolean {
        nodes.add(identifier)
        return true
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}