package processor

class IntMatrix (val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)
    private val elements = Array<IntArray>(rows) { IntArray(cols) { 0 } }

    override fun toString(): String = elements.map { it.joinToString(" ") }
        .joinToString("\n")

    operator fun plus(other: IntMatrix): IntMatrix? {
        if (this.size != other.size) return null
        val matrixSum = IntMatrix(rows, cols)
        elements.mapIndexed { i, row -> matrixSum[i] = sum(row, other[i]) }
        return matrixSum
    }

    operator fun times(scalar: Int): IntMatrix {
        val lineArray = IntArray(cols) { scalar }
        val scalarProduct = IntMatrix(rows, cols)
        elements.mapIndexed { i, row -> scalarProduct[i] = product(row, lineArray) }
        return scalarProduct
    }

    operator fun set(i: Int, elements: IntArray) { this.elements[i] = elements }
    operator fun get(i: Int): IntArray = elements[i]

    private fun sum(a: IntArray, b: IntArray): IntArray =
        a.mapIndexed { i, n -> n + b[i] }.toIntArray()

    private fun product(a: IntArray, b: IntArray): IntArray =
        a.mapIndexed { i, n -> n * b[i] }.toIntArray()
}

fun main() {
    val a = intMatrix()
    val scalar = readInts(1).first()
    println(a * scalar)
}

private fun intMatrix(): IntMatrix {
    val (rows, cols) = readInts(2)
    val matrix = IntMatrix(rows, cols)
    repeat(rows) { matrix[it] = readInts(cols) }
    return matrix
}

private fun readInts(count: Int) = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()