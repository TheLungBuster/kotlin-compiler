package com.enteld.paramsreader.di

import com.enteld.paramsreader.ParamsReader
import org.koin.dsl.module

val ParamsReaderModule = module {
	single { (args: Array<String>?) -> ParamsReader(args) }
}