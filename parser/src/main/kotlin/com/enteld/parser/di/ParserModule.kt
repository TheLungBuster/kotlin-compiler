package com.enteld.parser.di

import com.enteld.core.token.Token
import com.enteld.parser.Parser
import org.koin.dsl.module

val ParserModule = module {
    single { (tokens: List<Token>) -> Parser(tokens) }
}