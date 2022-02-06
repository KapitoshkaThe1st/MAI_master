package org.mai.dep210.scala.iris

import PetalSize.PetalSize

import scala.io.Source
import scala.util.Try

object IrisExample extends App {

//  val flowers = loadFromFile("iris.data")
  val flowers = loadFromFile("iris_trunc.data")
  //println(flowers)

  //get average sepal width
  println("average sepal width")
  val avgSepalWidth = flowers.view.map(_.sepalWidth).sum / flowers.length
  println(avgSepalWidth)

  //get average petal square - petal width multiplied on petal length
  println("average petal square - petal width multiplied on petal length")
  val avgPetalSquare = flowers.view.map(x => x.petalLength * x.petalWidth).sum / flowers.length
  println(avgPetalSquare)

  //get average petal square for flowers with sepal width > 4
  println("average petal square for flowers with sepal width > 4")
  val filteredFlowers = flowers.filter(_.sepalWidth > 4.0)
  val avgPetalSquareSelective = filteredFlowers.view.map(x => x.petalLength * x.petalWidth).sum / filteredFlowers.length
  println(avgPetalSquareSelective)

  //get flowers grouped by Petal size (PetalSize.Small, etc.) with function getPetalSize
  println("flowers grouped by Petal size (PetalSize.Small, etc.) with function getPetalSize")
  val groupsByPetalSize = flowers.groupBy(getPetalSize)
  groupsByPetalSize.foreach(g => { println(g._1 + ": "); g._2.foreach(println) })

  //get max sepal width for flowers grouped by species
  println("max sepal width for flowers grouped by species")
  val maxSepalWidthForGroupsBySpecies = flowers.view.groupBy(_.species).mapValues(_.maxBy(_.sepalWidth).sepalWidth)
  maxSepalWidthForGroupsBySpecies.foreach(s => { println(f"${s._1}: ${s._2}"); })

  def loadFromFile(path: String): List[Iris] = {
    val file = Source
      .fromFile(path)
    val result = file
      .getLines
      .map(line => line.toIris)
      .filter{
        case Some(_) => true
        case None => false
      }
      .map{
        case Some(iris) => iris
      }
      .toList
    file.close()
    result
  }

  implicit class StringToIris(str: String) {
    def toIris: Option[Iris] = str.split(",") match {
      case Array(a,b,c,d,e) if isDouble(a) && isDouble(b) && isDouble(c) && isDouble(d) =>
        Some(
          Iris(
            a.toDouble,
            b.toDouble,
            c.toDouble,
            d.toDouble,
            e))
      case _ => None
    }

    def isDouble(str: String): Boolean = Try(str.toDouble).isSuccess
  }

  def getPetalSize(iris: Iris): PetalSize = {
    val petalSquare = iris.petalLength * iris.petalWidth
    if(petalSquare < 2.0)
      PetalSize.Small
    else if(petalSquare < 5.0)
      PetalSize.Medium
    else
      PetalSize.Large
  }

}

object PetalSize extends Enumeration {
  type PetalSize = Value
  val Large, Medium, Small = Value
}

case class Iris(sepalLength: Double, sepalWidth: Double, petalLength: Double, petalWidth: Double, species: String)
