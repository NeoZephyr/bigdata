scala2.11.8

export SCALA_HOME=
export PATH=

```scala
object HelloWorld {
    def main(args : Array[String]) {
        println("HelloWorld...")
    }
}
```
scalac HelloWorld.scala
scala HelloWorld

```scala
val money = 100
val age:Int = 20
val height:Float = 1.65f
val days = 20.asInstanceOf[Double]
val isInt = 10.isInstanceOf[Int]
```
```scala
var name:String = "jack"
name = "pain"
```
```scala
lazy val a = 1
```

```scala
def max(x: Int, y: Int): Int = {
    if (x > y) {
        return x
    } else {
        return y
    }
}

def hello(): Unit = {
    println("hello")
}
```
```scala
println(max(x = 100, y = 200))
```

```scala
def printName(name: String = "jack"): Unit = {
    println(name)
}
```

```scala
def sum(numbers: Int*): Int = {
    var result = 0
    for (number <- numbers) {
        result += number;
    }

    return result;
}
```
```scala
1 to 10
1.to(10)

Range(1, 10)
Range(1, 10, 2)

1 until 10

for (i <- 1 to 10 if i % 2 == 0) {
    println(i)
}

val courses = Array("pain", "jack", "slog")
courses.foreach(course => println(sourse))

var (num, sum) = (100, 0)
while (num > 0) {
    sum = sum + num
    num = num - 1
}
println(sum)
```


```scala
object Demo {
    def main(args: Array[String]): Unit = {
        val stu = new Student()
        stu.name = "jack"
        println(stu.name + " ... " + stu.age)
        stu.study("english")

        val teacher = new Teacher("jack", 40)
    }

    class Student {
        var name:String = ""
        var address:String = _
        val age:Int = 10

        // class 内访问
        private [this] val gender = "male"

        def eat():String = {
            name + " eat..."
        }

        def study(book: String): Unit = {
            println(book + " study")
        }
    }

    class Teacher(val name:String, val age:Int) {
        println("before")

        val address = "beijing"
        val gender:String = _

        println("after")

        def this(name:String, age:Int, gender:String) {
            this(name, age)
            this.gender = gender
        }
    }

    class NiceTeacher(name:String, age:Int, var major:String) extends Teacher(name, age) {
        override val address = "shanghai"

        override def toString: String = "NiceTeacher: override def toString"
    }
}
```
```scala
abstract class Phone {
    def sendMsg

    val name:String
    val price:Int
}

class Xiaomi extends Phone {
    override def name: String = "Xiaomi"
    override def price: Int = 100

    override def sendMsg: Unit = {}
}
```

伴生对象与伴生类
```scala
class ApplyTest {
    def apply() = {
        println("class apply")
    }
}

object ApplyTest {
    var count = 0

    def incr = {
        count = count + 1
    }

    def apply() = {
        println("object apply")
    }
}

ApplyTest()
val a = new ApplyTest()
a()
```

```scala
case class Cat(name: String) 
```
```scala
println(Cat("jack").name)
```


```scala
object ArrayApp extends App {
    val a = new Array[String](5)
    a(1) = "hello"

    val b = Array("hadoop", "storm")

    b.mkString(",")

    val d = scala.collection.mutable.ArrayBuffer[Int]()
    d += 1
    d += 2
    d += (3, 4, 5)
    d ++= Array(6, 7, 8)

    val e = d.toArray()

    for (i <- 0 until d.length) {
        println(d(i))
    }

    for (i <- (0 until d.length).reverse) {
        println(d(i))
    }
}
```

```scala
val l = List(1, 2, 3, 4)
val l1 = 1 :: Nil

val l2 = scala.collection.mutable.ListBuffer[Int]()
l2 += 2
l2 += (3, 4, 5)
l2 += List(6, 7, 8, 9)
l2 -= 5

val l3 = l2.toList()
```
```scala
val s = Set(1, 1, 2, 3)
val s1 = scala.collection.mutable.Set[Int]()
```