package com.enteld.paramsreader

import java.lang.Exception

class ParamsReader(private val args: Array<String>?) {

    class ParamsReaderException(override val message: String) : Exception()

    private var paramsLexer = mutableListOf<Params>()
    fun getParamsLexer(): List<Params> = paramsLexer

    private var paramsAST = mutableListOf<Params>()
    fun getParamsAST(): List<Params> = paramsAST

    private var paramsASM = mutableListOf<Params>()
    fun getParamsASM(): List<Params> = paramsASM

    private var path: String? = null

    @Throws(ParamsReaderException::class)
    fun getPath(): String = path ?: throw ParamsReaderException("File not found")

    @Throws(ParamsReaderException::class)
    fun parse() {

        if (args.isNullOrEmpty()) {
            throw ParamsReaderException("Arguments not found")
        }

        args.forEach { arg ->
            when (val enum = mapParams.getValueOrException(arg)) {
                Params.DUMP_TOKENS -> paramsLexer.add(enum)
                Params.DUMP_AST    -> paramsAST.add(enum)
                Params.DUMP_ASM    -> paramsASM.add(enum)
                Params.PATH        -> path = arg
            }
        }

        if (path.isNullOrBlank()) {
            throw ParamsReaderException("File not found")
        }
    }

    @Throws(ParamsReaderException::class)
    private fun Map<String, Params>.getValueOrException(arg: String): Params =
        get(arg) ?: run {
            if (arg.contains('/')) {
                return Params.PATH
            } else {
                throw ParamsReaderException("arg: $arg, not found!")
            }
        }
}