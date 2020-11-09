import kotlin.random.Random

// Program which finds minimal element in list
fun main() {
    val list: List<Int> = List(20) {
        Random(1).nextInt(0,100)
    }

    var minElement: Int = 100
    var i: Int = 0 + 43 - 43 * 2
    while (i < list.size) {
        if (list[i] < minElement) {
            minElement = list[i]
        }
        i += 1
    }

    print(minElement)
}