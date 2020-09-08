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

object OutputTerm : KoinComponent {

    private val termColors by inject<TermColors>()

    operator fun invoke() = termColors

    fun printError(message: String) = println(this().red(message))

    inline fun <V> catchPrintErrorOrValue(func: () -> V): V {
        return try {
            func()
        } catch (exception: Throwable) {
            exception.message?.let { printError(it) }
            exitProcess(-1)
        }
    }

    private fun dump(impl: DumpPrint, params: List<Params>, path: String) =
        impl.printDump(params, path)

    private fun dumpToFile(impl: DumpPrint, params: List<Params>, path: String) =
        impl.printDumpToFile(params, path)

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
        return catchPrintErrorOrValue(task).also {
            println(this().green(endMessage))
            dump(dumper, params, fileName)
            dumpToFile(dumper, params, fileName)
            if (dumpErrors(dumper, fileName)) {
                onErrors.invoke()
            }
        }
    }

    private fun printWarning(message: String) = println(termColors.yellow(message))

    private fun dumpErrors(impl: DumpPrint, path: String): Boolean =
        impl.printErrors(path)

}