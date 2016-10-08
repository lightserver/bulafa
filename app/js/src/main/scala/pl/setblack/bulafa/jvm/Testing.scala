package pl.setblack.bulafa.jvm

object Testing {
  implicit class TestDescription(value: String) {
    def in(body: => Unit) =
      body
  }

  def main(args: Array[String]): Unit = {
    "mixing in dependencies to fixture works" in new Fixture {
      println(method1(dep1)(dep2, dep3))/* chyba trafiłem na błąd kompilatora,
      bo muszę tutaj implicit arugmenty podawać*/
    }

    "mixing in dependencies inline works" in {
      val setup = new DepSrc2 with DepSrc3
      import setup._ // tutaj wciągamy sobie pola ze stałej setup w zasięg
      val dep1 = new Dep1
      println(method1(dep1)) // reszta parametrów jest brana z setup
      // bo je zaimportowaliśmy
    }
  }

  class Dep1
  class Dep2
  class Dep3

  trait DepSrc1 {
    implicit val dep1 = new Dep1
  }

  trait DepSrc2 {
    implicit val dep2 = new Dep2
  }

  trait DepSrc3 {
    implicit val dep3 = new Dep3
  }

  trait Fixture extends DepSrc1 with DepSrc2 with DepSrc3

  def method1(dep1: Dep1)(implicit dep2: Dep2, dep3: Dep3): Int =
    method2(dep1, dep2) /* argumenty zawsze można podać explicit */ + method3

  def method2(implicit dep1: Dep1, dep2: Dep2): Int =
    method4 // tu argument leci implicit

  def method3(implicit dep3: Dep3): Int =
    3

  def method4(implicit dep1: Dep1): Int =
    8
}
