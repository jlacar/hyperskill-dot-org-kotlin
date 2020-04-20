package processor

class Matrix(val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)
    private val elements = Array<DoubleArray>(rows) { DoubleArray(cols) { 0.0 } }

    override fun toString(): String = elements.joinToString("\n") {
        it.joinToString(" ")
    }

    operator fun plus(other: Matrix): Matrix? {
        if (this.size != other.size) return null
        val matrixSum = Matrix(rows, cols)
        elements.mapIndexed { i, row -> matrixSum[i] = sum(row, other[i]) }
        return matrixSum
    }

    operator fun times(scalar: Double): Matrix {
        val scalarProduct = Matrix(rows, cols)
        elements.mapIndexed { i, row -> scalarProduct[i] = product(scalar, row) }
        return scalarProduct
    }

    fun transpose(): Matrix {
        val transposed = Matrix(cols, rows)
        repeat(rows) { row ->
            elements[row].forEachIndexed { col, n ->
                transposed[col][row] = n
            }
        }
        return transposed
    }

    operator fun set(row: Int, values: DoubleArray) {
        this.elements[row] = values
    }

    operator fun set(row: Int, col: Int, value: Double) {
        this.elements[row][col] = value
    }

    operator fun get(row: Int): DoubleArray = elements[row]

    operator fun get(row: Int, col: Int): Double = elements[row][col]

    private fun sum(a: DoubleArray, b: DoubleArray): DoubleArray =
            a.mapIndexed { i, n -> n + b[i] }.toDoubleArray()

    private fun product(scalar: Double, a: DoubleArray): DoubleArray =
            a.map { it * scalar }.toDoubleArray()

    private fun product(a: DoubleArray, b: DoubleArray): Double =
            a.foldIndexed(0.0) { i, sum, value -> sum + value * b[i] }

    operator fun times(other: Matrix): Matrix? {
        if (this.cols != other.rows) return null
        val product = Matrix(this.rows, other.cols)
        val otherColumns = other.transpose()
        for (row in 0 until this.rows) {
            for (col in 0 until other.cols) {
                product[row][col] = product(elements[row], otherColumns[col])
            }
        }
        return product
    }
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
    println("""|1. Add matrices
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
    val scalar = readDoubles(1).first()
    println(scalar * a)
}

private fun matrixProduct() {
    val (a, b) = Pair(matrix(), matrix())
    println(a * b)
}

private fun matrix(): Matrix {
    val (rows, cols) = readInts(2)
    val matrix = Matrix(rows, cols)
    repeat(rows) { matrix[it] = readDoubles(cols) }
    return matrix
}

private fun readInts(count: Int): IntArray = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()

private fun readDoubles(count: Int): DoubleArray = readLine()!!.trim().split(" ")
        .map { it.toDouble() }.take(count).toDoubleArray()