package com.enteld.core.output

import com.enteld.paramsreader.Params
import com.github.ajalt.mordant.TermColors
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.module
import kotlin.system.exitProcess

val OutputTermModule = module {
    single { TermColors() }
}

object OutputTerm : KoinComponent, Dumper {

    private val termColors by inject<TermColors>()

    operator fun invoke() = termColors

    fun printError(message: String) = println(this().red(message))

    inline fun <V> catchPrintErrorOrValue(func: () -> V, onErrors: () -> Unit = {}): V {
        return try {
            func()
        } catch (exception: Throwable) {
            exception.message?.let { printError(it) }
            onErrors()
            exitProcess(-1)
        }
    }

    fun <D> runWrapper(
        startMessage: String,
        loadMessage: String,
        task: () -> D,
        endMessage: String,
        dumper: DumpPrint,
        params: List<Params>,
        fileName: String,
        onErrors: () -> Unit = {},
        onWarnings: () -> Unit = {}
    ): D {
        println(this().reset(startMessage))
        println(this().blue(loadMessage))
        return catchPrintErrorOrValue(task, onErrors).also {
            var flagErrors = false
            if (dumper is DumpPrint) {
                dumpPrint(dumper, params, fileName)
            }
            if (dumper is DumpErrors && dumpErrors(dumper, fileName)) {
                flagErrors = true
                onErrors.invoke()
            }
            if (dumper is DumpWarnings && dumpWarnings(dumper, fileName)) {
                onWarnings.invoke()
            }
            if (!flagErrors) {
                println(this().green(endMessage))
            }
        }
    }
}