package com.enteld.ast.nodes.di

import com.enteld.ast.nodes.XMLVisitorImpl
import org.koin.dsl.module

val ASTModule = module {
    factory { XMLVisitorImpl() }
}