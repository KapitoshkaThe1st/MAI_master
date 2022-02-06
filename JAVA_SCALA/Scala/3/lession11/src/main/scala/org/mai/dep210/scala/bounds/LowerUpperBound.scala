package org.mai.dep210.scala.bounds

object LowerUpperBound extends App {
  val animal = new Animal
  val dog = new Dog
  val shep = new Shepherd
  val germanShep = new GermanShepherd
  val cat = new Cat

  val animalCarer = new AnimalCarer

  //animalCarer.upperDisplay(animal)
  animalCarer.upperDisplay(dog)
  animalCarer.upperDisplay(shep)
//  animalCarer.upperDisplay(cat)
  animalCarer.upperDisplay(germanShep)

  animalCarer.lowerDisplay(animal)
  animalCarer.lowerDisplay(shep)
  animalCarer.lowerDisplay(dog)
  animalCarer.lowerDisplay(cat)
  animalCarer.lowerDisplay(germanShep)


  animalCarer.bark(germanShep)
}


class Animal
class Dog extends Animal {
  def bark(): Unit = {println("B-b-bark")}
}
class Shepherd extends Dog
class Cat extends Animal

class GermanShepherd extends Shepherd {
  override def bark(): Unit = {println("BARK BARK")}
}

class AnimalCarer{
  def upperDisplay [T <: Dog](t: T): Unit = {
    println(s"Upper: $t")
  }

  def lowerDisplay [T >: Dog](t: T): Unit = {
    println(t.getClass.getName)
    println(s"Lower: $t")
  }

  def bark [T <: Dog](t: T): Unit = {
    t.bark()
  }
}