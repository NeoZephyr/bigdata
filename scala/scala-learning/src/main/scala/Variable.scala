object Variable {

  def main(args: Array[String]): Unit = {
    val name: String = "jack"
    val age: Int = 28
    val height: Float = 1.65F
    println(s"hello, name: ${name}, age: ${age}, height: ${height}")

    lazy val lazyVal = lazyTest()

    println("use lazyVal")
    println(s"lazy val: ${lazyVal}")

    implicit def transform(value: Double) : Int = {
      return value.toInt
    }

    val value: Int = 3.5

    println(s"transform value = ${value}")
  }

  def lazyTest(): String = {
    println("return lazyVal...")
    return "lazy"
  }
}
