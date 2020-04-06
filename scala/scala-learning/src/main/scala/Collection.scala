object Collection {
  def main(args: Array[String]): Unit = {

    println("array")
    testArray()

    println("list")
    testList()
  }

  def testArray(): Unit = {
    val players = Array("durant", "taylor", "lorry", "james")
    println(players(0))
    println(players.mkString(" & "))

    for (player <- players) {
      printf(s"${player} ")
    }

    players.foreach(println)
    players.reverse.foreach(println)
  }

  def testList(): Unit = {
    var lines = List("spark streaming", "kafka streaming", "kafka spark", "spark hbase", "spark hive", "spark sql")
    lines = lines.filter(lines => !lines.contains("hbase"))

    val nestedWords: List[Array[String]] = lines.map(e => e.split(" "))
    println(nestedWords.flatten)

    val flatWords: List[String] = lines.flatMap(e => e.split(" "))
    println(flatWords)

    val wordToListMap = flatWords.groupBy(word => word)
    println(wordToListMap)

    val wordToCount = wordToListMap.map(e => (e._1, e._2.size))
    println(wordToCount)

    val sortWordList = wordToCount.toList.sortWith((left, right) => {left._2 > right._2})
    println(sortWordList.take(3))

    println(sortWordList.mkString("|"))

    var nums = List(1, 2, 3, 4, 5)
    println(nums.reduce((left, right) => left - right))
    println(nums.reduce(_ + _))
    println(nums.fold(100)(_ + _))

    val ints = List(1, 2, 3, "hello").collect {
      case i: Int => i + 10
    }

    println(ints)
  }
}
