package com.enteld.lexer.ext

import com.enteld.core.token.Positions

internal fun Positions.copyPoint() = copy(start = start.copy(), end = end.copy())

internal fun Positions.isNextLine(char: Char) {
    if (char == '\n') {
        end.row++
        end.column = 1
    } else {
        end.column++
    }
}

internal fun Positions.update() {
    start = end.copy()
}