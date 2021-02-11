package basic

object FunctionApp {

    def main(args: Array[String]): Unit = {

        println(s"max(5, 10) = ${max(5, 10)}")
        println(s"sum(1, 2, 3, 4, 5) = ${sum(1, 2, 3, 4, 5)}")

        hello()

        val helloFunc = getHelloFunc()
        helloFunc("jack")
    }

    def max(x: Int, y: Int): Int = {
        if (x > y) {
            x
        } else {
            y
        }
    }

    def sum(nums: Int*): Int = {
        var result = 0
        for (elem <- nums) {
            result += elem
        }

        result
    }

    def hello(name: String = "pain"): Unit = {
        println(s"hello, ${name}")
    }

    def getHelloFunc() = {
        hello _
    }

}
