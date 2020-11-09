package com.enteld.lexer

import com.enteld.core.output.DumpErrors
import com.enteld.core.output.DumpPrint
import com.enteld.core.output.OutputTerm
import com.enteld.core.token.Positions
import com.enteld.core.token.Token
import com.enteld.lexer.ext.*
import com.enteld.lexer.ext.isNextLine
import com.enteld.lexer.ext.update
import com.enteld.paramsreader.Params
import org.koin.core.KoinComponent
import java.io.FileReader
import java.io.PushbackReader

class Lexer(bufferedReader: FileReader) : KoinComponent, DumpPrint, DumpErrors {

    class LexerException(override val message: String) : Exception(message)

    companion object {
        const val EMPTY_STRING = ""
        const val EOF_FILE = 65535
        const val END_FILE = -1
    }

    override val errors: MutableMap<Token, String> = mutableMapOf()

    private val tokens = mutableListOf<Token>()

    private val reader = PushbackReader(bufferedReader)
    private fun getAndSetPositions() =
        reader.get {
            buffer += it.toChar()
            positions.isNextLine(it.toChar())
        }

    private fun skipSetPosition() =
        reader.skip { positions.isNextLine(it.toChar()) }

    private var buffer = EMPTY_STRING

    private val positions by lazy {
        Positions().apply {
            start = Positions.Point()
            end = Positions.Point()
        }
    }

    @Throws(LexerException::class)
    fun parseFile(): List<Token> {
        do {
            tokens.add(fetchToken())
            positions.update()
            buffer = EMPTY_STRING
        } while (tokens.last().type != Token.Type.End)
        reader.close()
        return tokens
    }

    private fun fetchToken(): Token {
        while (reader.peek().toChar().isWhitespace()) {
            skipSetPosition()
        }
        positions.update()
        val char = getAndSetPositions()
        return if (char == END_FILE || char == EOF_FILE) {
            Token(
                type = Token.Type.End,
                lexeme = buffer,
                positions = positions.copyPoint()
            )
        } else {
            findType()
        }
    }

    private fun findType(): Token =
        when (buffer) {
            "." -> isRangeOrDot()
            "\"", "\'" -> isString()
            "/" -> isCommentOrOperation()
            ":" -> isColonOrElvis()
            "!" -> isOperationOrForecast()
            "?" -> isNullableOrSafeCall()
            "|", "&" -> isOperationOrUnknown()
            "<", ">", "=", "-", "+", "%", "*" -> isOperation()
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> isNumber()
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "_" -> isKeywordOrIdentifier()
            else -> isAlone()
        }

    private fun isNullableOrSafeCall(): Token =
        if (reader.peek().toChar() == '.') {
            getAndSetPositions()
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.OperatorSafeCall
            )
        } else {
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.OperatorNullable
            )
        }

    private fun isRangeOrDot(): Token =
        if (reader.peek().toChar() == '.') {
            getAndSetPositions()
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.OperatorRange
            )
        } else isAlone()

    private fun isOperationOrUnknown(): Token =
        if (reader.peek().toChar() == '&' || reader.peek().toChar() == '|')
            composeOperation() else {
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.Unknown
            )
        }

    private fun isOperationOrForecast(): Token =
        if (reader.peek().toChar() == '!') composeOperation() else operationAlone()

    private fun isAlone(): Token =
        Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = when (buffer) {
                "(" -> Token.Type.LParen
                ")" -> Token.Type.RParen
                "[" -> Token.Type.LBox
                "]" -> Token.Type.RBox
                "{" -> Token.Type.LFigured
                "}" -> Token.Type.RFigured
                ":" -> Token.Type.Colon
                "." -> Token.Type.Dot
                "," -> Token.Type.Comma
                else -> Token.Type.Unknown
            }
        )

    private fun isKeywordOrIdentifier(): Token {
        while (patternIdentifier(reader.peek())) {
            getAndSetPositions()
        }
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = when (buffer) {
                "import" -> Token.Type.KeywordImport
                "package" -> Token.Type.KeywordPackage
                "Any" -> Token.Type.TypeAny
                "Int" -> Token.Type.TypeInt
                "Double" -> Token.Type.TypeDouble
                "String" -> Token.Type.TypeString
                "Boolean" -> Token.Type.TypeBoolean
                "List" -> Token.Type.TypeList
                "Unit" -> Token.Type.TypeUnit
                "true" -> Token.Type.LiteralTrue
                "false" -> Token.Type.LiteralFalse
                "val" -> Token.Type.KeywordVal
                "var" -> Token.Type.KeywordVar
                "fun" -> Token.Type.KeywordFun
                "return" -> Token.Type.KeywordReturn
                "if" -> Token.Type.KeywordIf
                "else" -> Token.Type.KeywordElse
                "while" -> Token.Type.KeywordWhile
                "do" -> Token.Type.KeywordDo
                "for" -> Token.Type.KeywordFor
                "in" -> Token.Type.KeywordIn
                "is" -> Token.Type.KeywordIs
                "when" -> Token.Type.KeywordWhen
                "class" -> Token.Type.KeywordClass
                "null" -> Token.Type.LiteralNull
                else -> Token.Type.Identifier
            }
        )
    }

    private fun isNumber(): Token {
        while (patternNumber(reader.peek())) {
            getAndSetPositions()
        }
        if (reader.peek().toChar() == '.') {
            return isDouble()
        }
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = Token.Type.LiteralInt
        )
    }

    private fun isColonOrElvis(): Token =
        if (reader.peek().toChar() == '?') {
            getAndSetPositions()
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.OperatorElvis
            )
        } else isAlone()


    private fun isOperation(): Token =
        when (reader.peek().toChar()) {
            '=', '+', '-', '>' -> composeOperation()
            else -> operationAlone()
        }

    private fun operationAlone(): Token =
        Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = when (buffer) {
                "=" -> Token.Type.OperatorAssignment
                "+" -> Token.Type.OperatorPlus
                "-" -> Token.Type.OperatorMinus
                "*" -> Token.Type.OperatorMultiply
                "/" -> Token.Type.OperatorDivision
                "%" -> Token.Type.OperatorMod
                "<" -> Token.Type.OperatorLessThen
                ">" -> Token.Type.OperatorGreaterThen
                "!" -> Token.Type.OperatorNot
                else -> Token.Type.Unknown
            }
        )

    private fun composeOperation(): Token {
        getAndSetPositions()
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = when (buffer) {
                "--" -> Token.Type.OperatorDecrement
                "++" -> Token.Type.OperatorIncrement
                "+=" -> Token.Type.OperatorPlusAndAssign
                "-=" -> Token.Type.OperatorMinusAndAssign
                "*=" -> Token.Type.OperatorMultiplyAndAssign
                "/=" -> Token.Type.OperatorDivideAndAssign
                "%=" -> Token.Type.OperatorModAndAssign
                "<=" -> Token.Type.OperatorLessThenOrEqual
                ">=" -> Token.Type.OperatorGreaterThanOrEqual
                "==" -> Token.Type.OperatorEqual
                "!=" -> Token.Type.OperatorNotEqual
                "&&" -> Token.Type.OperatorAnd
                "||" -> Token.Type.OperatorOr
                "!!" -> Token.Type.OperatorForecast
                "->" -> Token.Type.OperatorLambda
                else -> Token.Type.Unknown
            }
        )
    }

    private fun isString(): Token {
        while (reader.peek() != END_FILE) {
            getAndSetPositions()
            if (buffer.first() == buffer.last()) {
                return Token(
                    lexeme = buffer.substring(1 until buffer.length - 1),
                    positions = positions.copyPoint(),
                    type = Token.Type.LiteralString
                )
            }
        }
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = Token.Type.Unknown
        )
    }

    private fun isCommentOrOperation(): Token =
        when (reader.peek().toChar()) {
            '/' -> isSLComment()
            '*' -> isMLComment()
            else -> isOperation()
        }

    private fun isMLComment(): Token {
        getAndSetPositions()
        while (reader.peek() != END_FILE) {
            getAndSetPositions()
            if (buffer.last() == '*') {
                if (getAndSetPositions().toChar() == '/') {
                    return Token(
                        lexeme = buffer
                            .substring(2 until buffer.length - 2)
                            .trim(),
                        positions = positions.copyPoint(),
                        type = Token.Type.Comment
                    )
                }
            }
        }
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = Token.Type.Unknown
        )
    }

    private fun isSLComment(): Token {
        getAndSetPositions()
        while (reader.peek().toChar() != '\n' && reader.peek() != END_FILE && reader.peek() != EOF_FILE) {
            getAndSetPositions()
        }
        return Token(
            lexeme = buffer,
            positions = positions.copyPoint(),
            type = Token.Type.Comment
        )
    }

    private fun isDouble(): Token {
        getAndSetPositions()
        return if (patternNumber(reader.peek())) {
            while (patternNumber(reader.peek())) {
                getAndSetPositions()
            }
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.LiteralDouble
            )
        } else {
            Token(
                lexeme = buffer,
                positions = positions.copyPoint(),
                type = Token.Type.Unknown
            )
        }
    }

    private val patternNumber: (Int) -> Boolean = {
        "0123456789".contains(it.toChar())
    }

    private val patternIdentifier: (Int) -> Boolean = {
        "0123456789_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(it.toChar())
    }

    override fun printDump(params: List<Params>, path: String) {
        if (params.contains(Params.DUMP_TOKENS)) {
            tokens.map { token ->
                when (token.type) {
                    Token.Type.Comment -> println(OutputTerm().gray(token.dump(path)))
                    Token.Type.Unknown -> println(OutputTerm().red(token.dump(path)))
                    else -> println(OutputTerm().white(token.dump(path)))
                }
            }
        } else tokens.map { if (it.type == Token.Type.Unknown) println(OutputTerm().red(it.dump(path))) }
    }

    override fun printErrors(path: String): Boolean {
        tokens.forEach {
            if (it.type == Token.Type.Unknown) {
                errors[it] = it.dump(path)
            }
        }
        return errors.isNotEmpty()
    }
}