package com.enteld.paramsreader

enum class Params {
    DUMP_TOKENS,
    DUMP_AST,
    DUMP_ASM,
    PATH
}

internal val mapParams = mapOf(
    "--dump-tokens" to Params.DUMP_TOKENS,
    "-dt" to Params.DUMP_TOKENS,

    "--dump-ast" to Params.DUMP_AST,
    "-dast" to Params.DUMP_AST,

    "--dump-asm" to Params.DUMP_ASM,
    "-dasm" to Params.DUMP_ASM
)
