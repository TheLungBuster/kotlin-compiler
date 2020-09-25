package com.enteld.core.token

data class Token(val type: Type, val lexeme: String, val positions: Positions) {

    enum class Type {
        KeywordClass,
        KeywordVal,
        KeywordVar,
        KeywordFun,
        KeywordFor,
        KeywordIn,
        KeywordIs,
        KeywordWhile,
        KeywordDo,
        KeywordIf,
        KeywordElse,
        KeywordWhen,
        KeywordPackage,
        KeywordImport,
        KeywordReturn,
        Unknown,
        Identifier,

        LParen,
        RParen,
        LFigured,
        RFigured,
        LBox,
        RBox,

        OperatorLessThen,
        OperatorGreaterThen,
        OperatorLessThenOrEqual,
        OperatorGreaterThanOrEqual,
        OperatorEqual,
        OperatorNotEqual,

        OperatorAnd,
        OperatorOr,
        OperatorNot,

        OperatorPlus,
        OperatorMinus,
        OperatorMultiply,
        OperatorDivision,
        OperatorMod,
        OperatorAssignment,
        OperatorIncrement,
        OperatorDecrement,
        OperatorPlusAndAssign,
        OperatorMinusAndAssign,
        OperatorMultiplyAndAssign,
        OperatorDivideAndAssign,
        OperatorModAndAssign,

        OperatorNullable,
        OperatorSafeCall,
        OperatorForecast,
        OperatorElvis,

        OperatorLambda,
        OperatorRange,

        Dot,
        Colon,
        Comma,

        TypeAny,
        TypeInt,
        TypeString,
        TypeDouble,
        TypeBoolean,
        TypeUnit,

        LiteralString,
        LiteralInt,
        LiteralDouble,
        LiteralTrue,
        LiteralFalse,
        LiteralNull,

        Comment,
        End
    }

    override fun toString() = "$positions type=$type\n\tlexeme=$lexeme"

    fun dump(path: String) = """
        type : "$type",
        lexeme : "$lexeme",
        position : $path: (${positions.start.row}, ${positions.start.column})

    """.trimIndent()

    fun dumpTab(path: String) = """    type : "$type",
    lexeme : "$lexeme",
    position : $path: (${positions.start.row}, ${positions.start.column})
    """
}