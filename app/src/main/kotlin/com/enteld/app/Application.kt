package com.enteld.app

import com.enteld.ast.nodes.di.ASTModule
import com.enteld.parser.Parser
import com.enteld.ast.nodes.scopes.RootScopeNode
import com.enteld.core.di.CoreModule
import com.enteld.core.output.OutputTerm.catchPrintErrorOrValue
import com.enteld.core.output.OutputTerm.runWrapper
import com.enteld.core.output.OutputTermModule
import com.enteld.core.output.ResourceReader
import com.enteld.core.token.Token
import com.enteld.lexer.Lexer
import com.enteld.lexer.di.LexerModule
import com.enteld.paramsreader.ParamsReader
import com.enteld.paramsreader.di.ParamsReaderModule
import com.enteld.parser.di.ParserModule
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    Application
    Application.start()

    val resourceReader = Application.getResourceReader()

    val paramsReader: ParamsReader = catchPrintErrorOrValue(
        func = { Application.getParamsReader(args).also { it.parse() } }
    )

    val file = File(paramsReader.getPath())

    val inputStream: FileReader = catchPrintErrorOrValue (
        func = { Application.readFile(file.absolutePath) }
    )

    val lexer = Application.getLexer(inputStream)

    val tokens: List<Token> = runWrapper(
        startMessage = resourceReader.getResource("lexer_start_message"),
        loadMessage = resourceReader.getResource("lexer_load_message"),
        endMessage = resourceReader.getResource("lexer_end_message"),
        task = lexer::parseFile,
        dumper = lexer,
        params = paramsReader.getParamsLexer(),
        fileName = file.name,
        onErrors = Application::exitProcess
    )

    val parser = Application.getParser(tokens)

    val tree: RootScopeNode = runWrapper(
        startMessage = resourceReader.getResource("parser_start_message"),
        loadMessage = resourceReader.getResource("parser_load_message"),
        endMessage = resourceReader.getResource("parser_end_message"),
        task = {
            parser.parse()
            parser.tree
        },
        dumper = parser,
        params = paramsReader.getParamsAST(),
        fileName = file.name,
        onErrors = Application::exitProcess
    )

    Application.stop()
}


object Application : KoinComponent {

    private fun initKoin() {
        startKoin {
            modules(
                CoreModule,
                OutputTermModule,
                ParamsReaderModule,
                LexerModule,
                ParserModule,
                ASTModule
            )
        }
    }

    fun start() = initKoin()

    fun stop() = stopKoin()

    fun getResourceReader(): ResourceReader = get()

    fun getParamsReader(args: Array<String>): ParamsReader =
        get(parameters = { parametersOf(args) })

    @Throws(FileNotFoundException::class)
    fun readFile(path: String) = FileReader(path)

    fun getLexer(inputStream: FileReader): Lexer =
        get(parameters = { parametersOf(inputStream) })

    fun getParser(tokens: List<Token>): Parser =
        get(parameters = { parametersOf(tokens) })

    fun exitProcess() {
        stop()
        exitProcess(-1)
    }
}
