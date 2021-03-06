package org.mai.dep210.scala.complex

import org.mai.dep210.scala.complex.Implicits._

object Main extends App {
  //define complex number 1+10i with similar real and imaginary types
  val c1 = Complex(1, 10)
  println(c1)

  //define complex number 1+10i with similar real and imaginary types
  val c2 = Complex(1, 10.0)
  println(c2)

  //define complex number 12+3i
  val c3:Complex[Int] = (12,3)
  println(c3)

  //define complex number 12.0+10.5i
  val c4 = 12.0.real + 10.5.imaginary
  println(c4)

  //define complex number with real part only
  val c5: Complex[Double] = 23.4
  println(c5)

  //define complex number with real part as Arithmetical and imaginary part
  val c6 = 20 + 10.imaginary
  println(c6)

  // arithmetic test
  // 13+20.5i
  println(c2 + c4)

  // -11-0.5i
  println(c2 - c4)

  // -93+130.5i
  println(c2 * c4)

  // 0.46017... +0.43067...i
  println(c2 / c4)
}
