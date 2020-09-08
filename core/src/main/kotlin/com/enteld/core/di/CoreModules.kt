package com.enteld.core.di

import com.enteld.core.Core
import com.enteld.core.token.Token
import com.enteld.core.token.TokenManager
import org.koin.dsl.module

val CoreModules = module {
    single { Core() }
    factory { (tokens: List<Token>) ->
        TokenManager(tokens)
    }
}