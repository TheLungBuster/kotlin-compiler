package com.enteld.core.token

class TokenManager(private var tokens: List<Token>) {
    private var position = 0

    init {
        tokens = tokens.filter { it.type != Token.Type.Comment }
    }

    fun get(): Token =
        if (position > tokens.size) tokens.last()
        else tokens[position].also {
            position++
        }

    fun peek() = tokens[position]

    fun peekType() = tokens[position].type
}