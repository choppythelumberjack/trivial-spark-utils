# trivial-spark-utils
Simple utility for Spark for Coproducts functionality emulated as TupleX[(None, None, Some(Thing3), ... None)] 
where a coproduct [Thing1, Thing2, Thing3... ThingN] is desired.

Here is a simple use case. Given the following entities
```
case class Person(...)
case class Robot(...)
```
You are you are attempting to create:
```
PersonOrRobot = Person | Robot
```
This can be emulated as:
```
(Option[Person], Option[Robot]) // where one variant will always be None
```
In order to achieve this without major boilerplate pains, this library can be used in the following way:
```scala
import import com.github.ctl.trivialspark.TupleSumFactories.TupleSumFactory2 // Factories exist for Tuple1-22

object Model {
  val factory = TupleSumFactory[Person, Robot]
  type PersonOrRobot = factory.TupleSum // a type alias for (Option[Person], Option[Robot])
}

object UserCode {
  val people: Dataset[Person] = ...
  val robots: Dataset[Robot] = ...
  val peopleOrRobots1: Dataset[PersonOrRobot] = people.map(factory.construct1(_)) // use 'construct1' to create (Some[Person], None)
  val peopleOrRobots2: Dataset[PersonOrRobot] = robots.map(factory.construct2(_)) // use 'construct2' to create (None, Some[Robot])
  // A union of this dataset can now be taken
  val union: Datset[PersonOrRobot] = peopleOrRobots1 union peopleOrRobots2
  // ...
}
```
This library then creates extensions methods for easily unpacking the coproduct-tuple
```scala
import com.github.ctl.trivialspark.TupleSumExtensions._

object UserCode {
  // ...
  val output: Dataset[SomethingElse] = union.map { personOrRobot =>
    personOrRobot.fold {
      (p: Person) => SomethingElse(p.name, p.age)
      (r: Robot) => SomethingElse(r.id, r.yearsService)
    }
  }
}
```
This allows easy mapping to some other data-type.
