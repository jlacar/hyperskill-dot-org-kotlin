package processor

fun main() {
    val (rowsA, colsA) = readMatrixSize()
    val matrixA = readMatrixElements(rowsA, colsA)

    val (rowsB, colsB) = readMatrixSize()
    if (rowsA != rowsB || colsA != colsB) {
        println("ERROR")
    } else {
        sumOf(matrixA, readMatrixElements(rowsB, colsB)).also {
            it.forEach { row -> println(row.joinToString(" ")) }
        }
    }
}

private fun readMatrixSize(): IntArray = readInts(2)

private fun readMatrixElements(rows: Int, cols: Int): Array<IntArray> {
    val matrix = Array<IntArray>(rows) { IntArray(cols) { 0 } }
    repeat(rows) { matrix[it] = readInts(cols) }
    return matrix
}

private fun readInts(count: Int) = readLine()!!.trim().split(" ")
        .map { it.toInt() }.take(count).toIntArray()

private fun sumOf(matrixA: Array<IntArray>, matrixB: Array<IntArray>): Array<IntArray> {
    val result = matrixA.mapIndexed { index, row -> rowMatrixSum(row, matrixB[index]) }
    return result.toTypedArray()
}

private fun rowMatrixSum(a: IntArray, b: IntArray): IntArray =
    a.mapIndexed { i, n -> n + b[i] }.toIntArray()
