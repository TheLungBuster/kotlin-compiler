package com.enteld.core.di

import com.enteld.core.Core
import com.enteld.core.output.ResourceReader
import com.enteld.core.token.Token
import com.enteld.core.token.TokenManager
import org.koin.dsl.module

val CoreModule = module {
    single { ResourceReader("ru") }
    factory { (tokens: List<Token>) ->
        TokenManager(tokens)
    }
}