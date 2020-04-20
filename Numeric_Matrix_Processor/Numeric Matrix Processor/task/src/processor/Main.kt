package processor

class Matrix(val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)

    private val values = Array(rows) { DoubleArray(cols) { 0.0 } }

    override fun toString(): String = values.joinToString("\n") {
        it.joinToString(" ")
    }

    fun transpose(): Matrix {
        val transposed = Matrix(cols, rows)
        (0 until rows).forEach { row ->
            (0 until cols).forEach { col ->
                transposed[col][row] = values[row][col]
            }
        }
        return transposed
    }

    operator fun set(row: Int, values: DoubleArray) {
        this.values[row] = values
    }

    operator fun set(row: Int, col: Int, value: Double) {
        this.values[row][col] = value
    }

    operator fun get(row: Int): DoubleArray = values[row]

    operator fun get(row: Int, col: Int): Double = values[row][col]

    operator fun plus(other: Matrix): Matrix? {
        if (this.size != other.size) return null
        val matrixSum = Matrix(rows, cols)
        values.mapIndexed { i, row -> matrixSum[i] = sum(row, other[i]) }
        return matrixSum
    }

    operator fun times(scalar: Double): Matrix {
        val scalarProduct = Matrix(rows, cols)
        values.mapIndexed { i, row -> scalarProduct[i] = product(scalar, row) }
        return scalarProduct
    }

    operator fun times(other: Matrix): Matrix? {
        if (this.cols != other.rows) return null
        val product = Matrix(this.rows, other.cols)
        val otherColumns = other.transpose()
        for (row in 0 until this.rows) {
            for (col in 0 until other.cols) {
                product[row][col] = product(values[row], otherColumns[col])
            }
        }
        return product
    }

    private fun sum(a: DoubleArray, b: DoubleArray): DoubleArray =
            a.mapIndexed { i, n -> n + b[i] }.toDoubleArray()

    private fun product(scalar: Double, a: DoubleArray): DoubleArray =
            a.map { it * scalar }.toDoubleArray()

    private fun product(a: DoubleArray, b: DoubleArray): Double =
            a.foldIndexed(0.0) { i, sum, value -> sum + value * b[i] }
}

// Make it commutative: scalar * Matrix == Matrix * scalar
operator fun Double.times(matrix: Matrix): Matrix = matrix.times(this)

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
    println(
    """|1. Add matrices
       |2. Multiply matrix to a constant
       |3. Multiply matrices
       |0. Exit
       |Your choice: """.trimMargin())
    return readLine()!!.trim().toInt()
}

private fun sum() {
    val (a, b) = Pair(matrix(), matrix())
    println(a + b)
}

private fun scalarProduct() {
    val a = matrix()
    val scalar = readDouble(1).first()
    println(scalar * a) // works too because of Double.times() extension above
}

private fun matrixProduct() {
    val (a, b) = Pair(matrix(), matrix())
    println(a * b)
}

private fun matrix(): Matrix {
    val (rows, cols) = readSize(2)
    val matrix = Matrix(rows, cols)
    repeat(rows) { matrix[it] = readDouble(cols) }
    return matrix
}

private fun readSize(count: Int): IntArray = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()

private fun readDouble(count: Int): DoubleArray = readLine()!!.trim().split(" ")
        .map { it.toDouble() }.take(count).toDoubleArray()