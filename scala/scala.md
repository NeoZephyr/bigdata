## 变量
```scala
var name : String = "pain"
var age : Int = 28
val height :Float = 1.65f

var address = "wuhan"
```
```scala
val name : String = "jack"
```
```scala
lazy val res = sum(10, 20)
```

```scala
var name = StdIn.readLine()
var price = StdIn.readDouble()
```

插值
```scala
println(s"name=${name}")
println(f"price=${price}%.2f")
println(raw"name=${name} \n")
```

自定义转换规则
```scala
implicit def transform(value: Double): Int = {
    return value.toInt()
}

val value: Int = 5.0
```


## 循环
```scala
for (i <- 1 to 5) {
    println(s"i = ${i}")
}

for (i <- 1 until 5) {
    println(s"i = ${i}")
}

for (i <- 1 to 5 if i != 2) {
    println(s"i = ${i}")
}
```
```scala
for (i <- range(1, 5, 2)) {
    println(s"i = ${i}")
}

var res = for (i <- range(1, 5, 2)) {
    yield i * 2
}
```

```scala
for (i <- 1 to 5) {
    if (i == 2) {
        Breaks.break()
    }
}

Breaks.breakable {
    for (i <- 1 to 5) {
        if (i == 2) {
            Breaks.break()
        }

        println(s"i = ${i}")
    }
}
```


## 函数
```scala
def max(x: Int, y: Int): Int = {
    if (x > y) {
        return x
    } else {
        return y
    }
}

println(max(x = 100, y = 200))
```

```scala
def sum(numbers: Int*): Int = {
    var result = 0
    for (number <- numbers) {
        result += number
    }

    return result
}
```

```scala
def hello(name : String = "pain") : Unit = {
    println(s"hello, ${name}")
}

hello()
hello(name = "jack")
```

函数作为返回值
```scala
def hello(name : String = "pain") : Unit = {
    println(s"hello, ${name}")
}

def getHelloFunc() = {
    hello _
}

getHelloFunc()("jack")
```

闭包
```scala
def f1(i : Int) = {
    def f2(j : Int) {
        return i * j
    }

    f2 _
}

f1(1)(2)
```

函数作为参数
```scala
def f(inner : (Int) => Int) : Int = {
    return inner(10) + 10
}

def inner(num : Int) : Int = {
    return num
}

f(inner)

f((x) => {
    println(x)
})
```

函数简化过程
```scala
def f(add : (Int, Int) => Int) : Int = {
    add(10, 20)
}

println(f((x: Int, y: Int) => { x + y }))
println(f((x, y) => { x + y }))
println(f((x, y) => x + y))
println(f(_ + _))
```
```scala
list.map((x: Int) => x + 1)
// 自动推断出类型
list.map((x) => x + 1)
// 只有一个参数，省略括号
list.map(x => x + 1)
// 使用占位符
list.map(_ + 1)

list.map(_ * 2).filter(_ > 8)

list.flatten
```
```scala
val text = scala.io.Source.fromFile("hello.txt").mkString
```

偏函数
```scala
def hello: PartialFunction[String, String] = {
    case "jack" => "jackson"
    case "johm" => "johnson"
    case _ => "no no no"
}
```

```scala
try {
    val r = 10 / 0
} catch {
    case ex: RuntimeException => println("runtime exception")
    case ex: Exception => println("catch exception")
} finally {
    println("scala finally")
}
```






## 类
```scala
class User(name: String) {
    var name: String = _
    var age: Int = _

    println(s"name = ${name}")

    def this() {
        this("jack")
    }

    def this(name: String, age: Int) {
        this(name)
        this.age = age
    }

    def printName() : Unit = {
        println(name)
    }
}

var user: User = new User("jack")
```

```scala
abstract class Monitor {
    def alter()
}

class KafkaMonitor extends Monitor {
    override def alter: Unit = {}
}
```

抽象属性、重写属性
```scala
abstract class Person(name: String) {
    var name: String = _
    var gender: String
    val address: String = "china"
}

class Man(name: String) extends Person(name) {
    var gender: String = "male"
    override val address: String = ""

    def this() {
        this(name)
    }
}
```

```scala
class User(var name: String) {}

var user = new User("hello")
println(user.name)
```

```scala
trait Sleep {}
trait Eat {}

class User extends Persion with Sleep with Eat {}
```
```scala
trait InsertOption {
    def insert() {
        println("insert")
    }
}

trait FileSystem extends InsertOption {
    override def insert() {
        println("file")
        super[InsertOption].insert()
    }
}
```
```scala
val mysql = new Mysql() with InsertOption
mysql.insert()
```

伴生对象与伴生类
```scala
class User {
    var name: String = "pain"
}

object User {    
    def apply(name: String): User = new User
}
```
```scala
val user = User("jack")
```


```scala
val players: Array[String] = Array("pain", "jack", "slog")

println(players(0))
println(players.mkString("|"))

for (player <- players) {
    println(player)
}

players.foreach(player => println(player))
players.foreach(println(_))
players.foreach(println)
```

```scala
var players: ArrayBuffer[String] = ArrayBuffer("pain", "tack")
players(0) = "jelin"
players.insert(0, "fony")
players += "taylor"
```

```scala
val list = List("001-jack", "001-pain", "002-page", "003-path")

println(s"sum = ${list.sum}")
println(s"reverse = ${list.reverse}")

val map = list.groupBy(e => e.substring(0, 3))
map.foreach(e => {println(e._1 + " = " + e._2)})

var sortList = list.sortBy(e => e.substring(4))

var ascList = list.sortWith((x, y) => {x < y})

var tuples = list.map(x => {(x, 1)})
```
```scala
val words = List("spark", "kafka", "spark", "hbase", "hive", "spark", "kafka")
val wordToListMap = words.groupBy(word => word)
val wordToCountMap = wordToListMap.map(e => {(e._1, e._2.size)})
val sortList = wordToCountMap.toList.sortWith((left, right) => {left._2 > right._2})
val resultList = sortList.take(3)

println(resultList.mkString(","))
```
```scala
val lines = List("Hello World", "Hello Scala", "Hello Hadoop")
lines.flatMap(e => e.split(" "))
```
```scala
val words = List("spark", "kafka", "spark", "hbase", "hive", "spark", "kafka")
val filterWords = words.filter(e => e.startWith("s"))
```
```scala
val tuples = list1.zip(list2)
```
```scala
val unionList = list1.union(list2)
```
```scala
val intersectList = list1.intersect(list2)
```
```scala
val diffList = list1.diff(list2)
```

```scala
list.reduce((left, right) => {left + right})
list.reduce(_ + _)
```

```scala
list.fold(100)(_ + _)
```

```scala
map1.folder(map2)((map, t) => {
    map(t._1) = map.getOrElse(t._1, 0) + t._2
    map
})
```


```scala
val set = Set(1, 1, 2, 3)
```

```scala
val map = Map("jack" -> 100, "pain" -> 90)
```

```scala
val tuple = ("jack", "28", "19000")
val (name, age, salary) = tuple
```

```scala
List(1, 2, 3, "hello").collect{case i: Int => i + 1}
```

```scala
def test[T <: User](t: T): Unit = {}

test[User](new User())

class Test[+User] {}
```

```scala
grade match {
    case "A" => println("Excellent")
    case "B" => println("Good")
    case _ if (name == "pain") => println("Good")
    case _ => println("Ok")
}
```
```scala
array match {
    case Array("jack") => println("one elem")
    case Array(x, y) => println(s"two elem, ${x}, ${y}")
    case Array("pain", _*) => println("many elem")
    case _ => println("unknow elem")
}
```
```scala
list match {
    case "jack"::Nil => println("one elem")
    case x::y::Nil => println(s"two elem, ${x}, ${y}")
    case "jack"::tail => println("many elem")
    case _ => println("unknow elem")
}
```
```scala
obj match {
    case x: Int => println("int")
    case s: String => println("string")
    case m: Map[_, _] => m.foreach(println)
    case _ => println("unknow type")
}
```
```scala
try {
    10 / 0
} catch {
    case e: ArithmeticException => println("can not be 0")
    case e: Exception => println(e.getMessage)
} finally {}
```
```scala
class Person
case class Student(name: String)
case class Teacher(name: String)

person match {
    case Student(name) => println("student")
    case Teacher(name) => println("teacher")
    case _ => println("other")
}
```


样例类
```scala
case class Cat(name: String) 
```
```scala
println(Cat("jack").name)
```
