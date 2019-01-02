package FizzBuzz

case class FizzBuzzRule(name:String, rule: Int => Boolean)

case class FizzBuzz(name:String,rules: List[FizzBuzzRule]) {
  def solve(numbers:List[Int]): List[String] = FizzBuzz.solve(numbers,rules)
}

object FizzBuzz {
  private def solve(numbers:List[Int],rules:List[FizzBuzzRule]): List[String] = {
    numbers.map { number =>
      solveNumber(number,rules)
    }
  }

  private def solveNumber(number:Int,rules:List[FizzBuzzRule]):String = {

    val solved:List[String] = rules.flatMap( rule =>
      if (rule.rule(number)) {
        Some(rule.name)
      } else {
        None
      }
    )

    if (solved.nonEmpty) {
      listToString(solved).trim
    } else {
      number.toString
    }
  }

  private def listToString(list: List[String]): String = list match{
    case s :: rest => s + " " + listToString(rest)
    case Nil => ""
  }
}

object Main extends App {
  val numbers = (1 to 100).toList
  val fizz = FizzBuzzRule("Fizz", number => Math.abs(number%3)==0)
  val buzz = FizzBuzzRule("Buzz", number => Math.abs(number%5)==0)
  val game = FizzBuzz("FizzBuzz Game", List(fizz,buzz))
  game.solve(numbers).foreach(number => println(number))

}
