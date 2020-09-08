import kotlin.random.Random

fun main() {
    val list = List(20) {
        Random(1).nextInt(0,100)
    }

    var minElement = Int.MAX_VALUE
    for (num in list) {
        if (num < minElement) {
            minElement = num
        }
    }

    print("Минимальный элемент: $minElement")
}