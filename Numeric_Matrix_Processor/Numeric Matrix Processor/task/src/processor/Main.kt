package processor

class IntMatrix (val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)
    private val elements = Array<IntArray>(rows) { IntArray(cols) { 0 } }

    override fun toString(): String = elements.map {
        it.joinToString(" ", postfix = "\n") }.joinToString(" ")

    operator fun plus(other: IntMatrix): IntMatrix? {
        if (this.size != other.size) return null
        val matrixSum = IntMatrix(rows, cols)
        elements.mapIndexed { i, row -> matrixSum[i] = sum(row, other[i]) }
        return matrixSum
    }

    private fun sum(a: IntArray, b: IntArray): IntArray =
        a.mapIndexed { i, n -> n + b[i] }.toIntArray()

    operator fun set(i: Int, elements: IntArray) { this.elements[i] = elements }
    operator fun get(i: Int): IntArray = elements[i]
}

fun main() {
    val (a, b) = Pair(intMatrix(), intMatrix())
    println((a + b) ?: "ERROR")
}

private fun intMatrix(): IntMatrix {
    val (rows, cols) = readInts(2)
    val matrix = IntMatrix(rows, cols)
    repeat(rows) { matrix[it] = readInts(cols) }
    return matrix
}

private fun readInts(count: Int) = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()