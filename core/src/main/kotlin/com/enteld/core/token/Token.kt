package com.enteld.core.token

data class Token(val type: Type, val lexeme: String, val positions: Positions) {

    enum class Type {
        KeywordClass,
        KeywordVal,
        KeywordVar,
        KeywordFun,
        KeywordFor,
        KeywordWhile,
        KeywordDo,
        KeywordIf,
        KeywordElse,
        KeywordWhen,
        KeywordImport,
        KeywordReturn,
        Unknown,

        LParen,
        RParen,
        LBracket,
        RBracket,
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

        OperatorEnd,
        OperatorOr,
        OperatorNot,

        OperatorPlus,
        OperatorMinus,
        OperatorMultiply,
        OperatorDivision,
        OperatorAssignment,
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
        Semicolon,
        Comma,

        TypeAny,
        TypeAnyNullable,
        TypeInt,
        TypeIntNullable,
        TypeString,
        TypeStringNullable,
        TypeDouble,
        TypeDoubleNullable,
        TypeLong,
        TypeLongNullable,
        TypeUnit,
        TypeUnitNullable,
        TypeFloat,
        TypeFloatNullable,

        LiteralString,
        LiteralInt,
        LiteralDouble,
        LiteralLong,
        LiteralFloat,
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

val plugToken: (Token.Type, String, Positions) -> Token = { type, lexeme, pos -> Token(type, lexeme, pos) }