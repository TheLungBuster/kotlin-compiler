
/* Program which finds
 * the biggest divider
 * of two numbers
 */

fun main() {

    var firstNumber: Int = 98
    var secondNumber: Int = 28

    while (firstNumber != secondNumber) {
        if (firstNumber > secondNumber) {
            firstNumber -= secondNumber
        } else {
            secondNumber -= firstNumber
        }
    }

    println(firstNumber)
}