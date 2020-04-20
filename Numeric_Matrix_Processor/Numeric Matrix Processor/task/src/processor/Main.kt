package processor

import processor.MatrixTransposeType.*

class Matrix(val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)

    private val values = Array(rows) { DoubleArray(cols) { 0.0 } }

    override fun toString(): String = values.joinToString("\n") { it.joinToString(" ") }

    fun transpose(strategy: MatrixTransposeType = MAIN_DIAGONAL) = strategy.transpose(this)

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

// Make scalar multiplication commutative: scalar * Matrix == Matrix * scalar
operator fun Double.times(matrix: Matrix) = matrix.times(this)

typealias TransposeMapper = (Matrix, Int, Int) -> Double

enum class MatrixTransposeType {
    MAIN_DIAGONAL {
        override fun transpose(matrix: Matrix) = map(matrix) { m, row, col -> m[col][row] }
    },
    SIDE_DIAGONAL {
        override fun transpose(matrix: Matrix) = map(matrix) { m, row, col -> m[m.cols - col - 1][m.rows - row - 1] }
    },
    VERTICAL_LINE {
        override fun transpose(matrix: Matrix) = map(matrix) { m, row, col -> m[row][m.cols - col - 1] }
    },
    HORIZONTAL_LINE {
        override fun transpose(matrix: Matrix) = map(matrix) { m, row, col -> m[m.rows - row - 1][col] }
    };

    protected fun map(matrix: Matrix, mapper: TransposeMapper): Matrix {
        val (rows, cols) = matrix.size
        val transposed = Matrix(cols, rows)
        (0 until rows).forEach { col ->
            (0 until cols).forEach { row ->
                transposed[row][col] = mapper(matrix, row, col)
            }
        }
        return transposed
    }

    abstract fun transpose(matrix: Matrix): Matrix
}

fun main() {
    do {
        val action = chooseAction()
        when (action) {
            1 -> sum()
            2 -> scalarProduct()
            3 -> matrixProduct()
            4 -> transposeMenu()
        }
    } while (action != 0)
}

private fun chooseAction(): Int {
    println(
    """|1. Add matrices
       |2. Multiply matrix to a constant
       |3. Multiply matrices
       |4. Transpose matrix
       |0. Exit
       |Your choice: """.trimMargin())
    return readInt(1).first()
}

private fun sum() = println((readMatrix() + readMatrix()) ?: "ERROR")

private fun scalarProduct() {
    val a = readMatrix()
    val scalar = readDouble(1).first()
    println(scalar * a) // works too because of Double.times() extension above
}

private fun matrixProduct() = println((readMatrix() * readMatrix()) ?: "ERROR")

private fun transposeMenu() {
    print(
    """|1. Main diagonal
       |2. Side diagonal
       |3. Vertical line
       |4. Horizontal line
       |Your choice: """.trimMargin()
    )
    doTransposeFor(typeChosen())
}

private fun typeChosen(): MatrixTransposeType {
    val ord = readInt(1).first() - 1
    return values().first { it.ordinal == ord }
}

private fun doTransposeFor(transposeStrategy: MatrixTransposeType) =
    println("The result is:\n${readMatrix().transpose(transposeStrategy)}")

private fun readMatrix(): Matrix {
    print("Enter matrix size: ")
    val (rows, cols) = readInt(2)
    val matrix = Matrix(rows, cols)
    println("Enter matrix:")
    repeat(rows) { matrix[it] = readDouble(cols) }
    return matrix
}

private fun readInt(count: Int): IntArray = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()

private fun readDouble(count: Int): DoubleArray = readLine()!!.trim().split(" ")
        .map { it.toDouble() }.take(count).toDoubleArray()