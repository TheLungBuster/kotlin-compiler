package com.enteld.lexer.di

import com.enteld.lexer.Lexer
import org.koin.dsl.module
import java.io.FileReader

val LexerModule = module {
    single { (inputStream: FileReader) -> Lexer(inputStream)}
}