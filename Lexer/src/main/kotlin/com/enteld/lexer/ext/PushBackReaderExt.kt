package com.enteld.lexer.ext

import java.io.PushbackReader

fun PushbackReader.get(action: (Int) -> Unit) =
    read().also { action(it) }


fun PushbackReader.peek() =
    read().also { unread(it) }


fun PushbackReader.skip(action: (Int) -> Unit) =
    read().also { action(it) }
