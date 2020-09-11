import kotlin.random.Random

// Program which finds minimal element in list
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

    print(minElement)
}