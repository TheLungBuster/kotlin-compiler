package com.enteld.core.output

import com.enteld.paramsreader.Params

interface Dumper {
    fun dumpPrint(impl: DumpPrint, params: List<Params>, path: String) = impl.printDump(params, path)
    fun dumpWarnings(impl: DumpWarnings, path: String): Boolean = impl.printWarnings(path)
    fun dumpErrors(impl: DumpErrors, path: String): Boolean = impl.printErrors(path)
}