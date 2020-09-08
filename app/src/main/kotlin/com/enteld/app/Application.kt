package com.enteld.app

import com.enteld.core.di.CoreModules
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get

fun main(args: Array<String>) {
    Application.start()
    Application.stop()
}


class Application() : KoinComponent {

    companion object : KoinComponent {

        private fun initKoin() {
            startKoin {
                modules(
                    CoreModules
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
    }
}
