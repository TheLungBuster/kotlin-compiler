package com.enteld.core.token

data class Positions(var start: Point = Point(), var end: Point = Point()) {
    data class Point(var column: Long = 1, var row: Long = 1)

    override fun toString() = "[${start.row}:${start.column}][${end.row}:${end.column}]"
}