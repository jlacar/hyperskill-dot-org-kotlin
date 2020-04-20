package processor

class IntMatrix(val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)
    private val elements = Array<IntArray>(rows) { IntArray(cols) { 0 } }

    override fun toString(): String = elements.joinToString("\n") {
        it.joinToString(" ")
    }

    operator fun plus(other: IntMatrix): IntMatrix? {
        if (this.size != other.size) return null
        val matrixSum = IntMatrix(rows, cols)
        elements.mapIndexed { i, row -> matrixSum[i] = sum(row, other[i]) }
        return matrixSum
    }

    operator fun times(scalar: Int): IntMatrix {
        val scalarProduct = IntMatrix(rows, cols)
        elements.mapIndexed { i, row -> scalarProduct[i] = product(scalar, row) }
        return scalarProduct
    }

    private fun transpose(): IntMatrix {
        val transposed = IntMatrix(cols, rows)
        repeat(rows) { row ->
            this[row].forEach { n ->
                transposed[row] = transposed[row] + n
            }
        }
        return transposed
    }

    operator fun set(row: Int, values: IntArray) {
        this.elements[row] = values
    }

    operator fun set(row: Int, col: Int, value: Int) {
        this.elements[row][col] = value
    }

    operator fun get(row: Int): IntArray = elements[row]

    operator fun get(row: Int, col: Int): Int = elements[row][col]

    private fun sum(a: IntArray, b: IntArray): IntArray =
            a.mapIndexed { i, n -> n + b[i] }.toIntArray()

    private fun product(scalar: Int, a: IntArray): IntArray =
            a.map { it * scalar }.toIntArray()

    private fun product(a: IntArray, b: IntArray): Int =
            a.foldIndexed(0) { i, sum, value -> sum + value * b[i] }

    operator fun times(other: IntMatrix): IntMatrix? {
        val (_, cols) = other.size
        if (this.rows != cols) return null

        val product = IntMatrix(this.rows, cols)
        val otherColumns = other.transpose()
        elements.forEachIndexed { row, rowMatrix ->
            repeat(rowMatrix.size) { col ->
                product[row][col] = product(rowMatrix, otherColumns[col])
            }
        }
        return product
    }
}

// Make it commutative: scalar * IntMatrix == IntMatrix * scalar
operator fun Int.times(matrix: IntMatrix): IntMatrix = matrix.times(this)

fun main() {
    do {
        val action = chooseAction()
        when (action) {
            1 -> sum()
            2 -> scalarProduct()
            3 -> matrixProduct()
        }
    } while (action != 0)
}

private fun chooseAction(): Int {
    println("""|1. Add matrices
        |2. Multiply matrix to a constant
        |3. Multiply matrices
        |0. Exit
        |Your choice: """.trimMargin())
    return readLine()!!.trim().toInt()
}

private fun sum() {
    val (a, b) = Pair(intMatrix(), intMatrix())
    println(a + b)
}

private fun scalarProduct() {
    val a = intMatrix()
    val scalar = readInts(1).first()
    println(scalar * a)
}

private fun matrixProduct() {
    val (a, b) = Pair(intMatrix(), intMatrix())
    println(a * b)
}

private fun intMatrix(): IntMatrix {
    val (rows, cols) = readInts(2)
    val matrix = IntMatrix(rows, cols)
    repeat(rows) { matrix[it] = readInts(cols) }
    return matrix
}

private fun readInts(count: Int): IntArray = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()