object Match {
  def main(args: Array[String]): Unit = {
    val grade = "C"
    val name = "jack"

    grade match {
      case "A" => println("Excellent")
      case "B" => println("Good")
      case _ if (name == "pain") => println("Good")
      case _ => println("Just so so")
    }

    val array = Array("jack", "tail", "butt")

    array match {
      case Array(_) => println("only one element")
      case Array(_, _) => println("two element")
      case Array(_*) => println("many element")
      case _ => println("unknown")
    }

    val list = List("pain", "slot")

    list match {
      case List(_) => println("only one element")
      case x::y::Nil => println(s"two element, x = ${x}, y = ${y}")
      case List(_, _) => println("two element")
      case List(_*) => println("many element")
      case _ => println("unknown")
    }
  }
}
