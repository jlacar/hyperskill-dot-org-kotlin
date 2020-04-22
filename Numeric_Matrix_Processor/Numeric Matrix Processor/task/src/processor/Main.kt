package processor

import processor.MatrixTransposeType.*

fun main() {
    do {
        println("""|1. Add matrices
               |2. Multiply matrix to a constant
               |3. Multiply matrices
               |4. Transpose matrix
               |5. Calculate a determinant
               |0. Exit
               |Your choice: """.trimMargin())

        val choice = mainMenuChoice()
        when (choice) {
            1 -> sum()
            2 -> scalarProduct()
            3 -> matrixProduct()
            4 -> transposeMenu()
            5 -> determinant()
        }
    } while (choice != 0)
}

private fun mainMenuChoice() = readInt(1).first()

private fun readInt(count: Int): IntArray = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()

private fun readDouble(count: Int): DoubleArray = readLine()!!.trim().split(" ")
        .map { it.toDouble() }.take(count).toDoubleArray()

private fun readMatrix(): Matrix {
    print("Enter matrix size: ")
    val (rows, cols) = readInt(2)
    val matrix = Matrix(rows, cols)
    println("Enter matrix:")
    repeat(rows) { matrix[it] = readDouble(cols) }
    return matrix
}

private fun printResultOrError(result: Any?) {
    println("""The result is:
        |${result ?: "ERROR"}""".trimMargin())
}

class Matrix(val rows: Int, val cols: Int) {
    val size = Pair(rows, cols)

    private val values = Array(rows) { DoubleArray(cols) { 0.0 } }

    override fun toString(): String = values.joinToString("\n") { it.joinToString(" ") }

    fun isSquare() = rows == cols
    fun isNotSquare() = !isSquare()

    fun transpose(strategy: MatrixTransposeType = MAIN_DIAGONAL): Matrix {
        val (cols, rows) = this.size
        val transposed = Matrix(rows, cols)
        val transposeFn = strategy.mapper()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                transposed[row][col] = transposeFn(this, row, col)
            }
        }
        return transposed
    }

    fun determinant(): Double? {
        if (isNotSquare()) return null
        return 0.0
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

    private fun sum(a: DoubleArray, b: DoubleArray): DoubleArray =
            a.mapIndexed { i, n -> n + b[i] }.toDoubleArray()

    operator fun times(scalar: Double): Matrix {
        val scalarProduct = Matrix(rows, cols)
        values.mapIndexed { i, row -> scalarProduct[i] = product(scalar, row) }
        return scalarProduct
    }

    private fun product(scalar: Double, a: DoubleArray): DoubleArray =
            a.map { it * scalar }.toDoubleArray()

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

    private fun product(a: DoubleArray, b: DoubleArray): Double =
            a.foldIndexed(0.0) { i, sum, value -> sum + value * b[i] }
}

// Make scalar multiplication commutative: scalar * Matrix == Matrix * scalar
operator fun Double.times(matrix: Matrix) = matrix.times(this)

/**
 * A function that calculates the value in the given Matrix that
 * maps to the given row and column of the transposed Matrix.
 * That is, given matrix a and the transpose function fn(),
 * then fn(a, row, col) ==> a.transposed(row, col)
 */
private typealias TransposeFunction = (Matrix, Int, Int) -> Double

enum class MatrixTransposeType {
    MAIN_DIAGONAL {
        override fun mapper(): TransposeFunction = { a, row, col -> a[col][row] }
    },
    SIDE_DIAGONAL {
        override fun mapper(): TransposeFunction = { a, row, col -> a[a.cols - col - 1][a.rows - row - 1] }
    },
    VERTICAL_LINE {
        override fun mapper(): TransposeFunction = { a, row, col -> a[row][a.cols - col - 1] }
    },
    HORIZONTAL_LINE {
        override fun mapper(): TransposeFunction = { a, row, col -> a[a.rows - row - 1][col] }
    };

    abstract fun mapper(): TransposeFunction
}

private fun sum() {
    printResultOrError(readMatrix() + readMatrix())
}

private fun scalarProduct() {
    val a = readMatrix()
    val scalar = readDouble(1).first()
    printResultOrError(scalar * a) // works too because of Double.times() extension above
}

private fun matrixProduct() {
    printResultOrError(readMatrix() * readMatrix())
}

private fun transposeMenu() {
    print("""|1. Main diagonal
             |2. Side diagonal
             |3. Vertical line
             |4. Horizontal line
             |Your choice: """.trimMargin())

    val type = transposeTypeChoice()
    printResultOrError(readMatrix().transpose(type))
}

private fun transposeTypeChoice(): MatrixTransposeType {
    val ord = readInt(1).first() - 1
    return values().first { it.ordinal == ord }
}

private fun determinant() {
    printResultOrError(readMatrix().determinant())
}