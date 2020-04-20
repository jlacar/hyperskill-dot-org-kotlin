package processor

fun main() {
    val a = Matrix(2, 3)
    a[0] = doubleArrayOf(1.0, 2.0, 1.0)
    a[1] = doubleArrayOf(3.0, 4.0, 2.0)

    val b = Matrix( 3, 2)
    b[0] = doubleArrayOf(2.0, 3.0)
    b[1] = doubleArrayOf(4.0, 5.0)
    b[2] = doubleArrayOf(1.0, 2.0)

    println("A:\n$a\n")
    println("B:\n$b\n")
    println("B':\n${b.transpose()}\n")
    println(Matrix(a.rows, b.cols))
    println("A * B:\n${a * b}")
}