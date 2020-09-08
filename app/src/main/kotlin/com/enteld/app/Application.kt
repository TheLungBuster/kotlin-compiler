package com.enteld.app

import com.enteld.core.di.CoreModule
import com.enteld.paramsreader.Params
import com.enteld.paramsreader.di.ParamsReaderModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.core.parameter.parametersOf

fun main(args: Array<String>) {
    Application.start()
    Application.stop()
}


class Application() : KoinComponent {

    companion object : KoinComponent {

        private fun initKoin() {
            startKoin {
                modules(
                    CoreModule,
                    ParamsReaderModule
                )
            }
        }

        fun start(): Application {
            initKoin()
            return get()
        }

        fun stop() {
            stopKoin()
        }

        fun getPReader(args: Array<String>): Params =
            get(parameters = { parametersOf(args) })
    }
}
