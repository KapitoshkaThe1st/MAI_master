package org.mai.dep210.scala.complex

import scala.language.implicitConversions

case class Complex[A : Arithmetic](re: A, im: A) {
  override def toString: String = s"$re+${im}i"

  def +(that: Complex[A]): Complex[A] = {
    val add = implicitly[Arithmetic[A]].add _
    Complex(add(re, that.re), add(im, that.im))
  }
  def -(that: Complex[A]): Complex[A] = {
    val subtract = implicitly[Arithmetic[A]].subtract _
    Complex(subtract(re, that.re), subtract(im, that.im))
  }
  def *(that: Complex[A]): Complex[A] = {
    val arithm = implicitly[Arithmetic[A]]
    val newRe = arithm.subtract(arithm.multiply(re, that.re), arithm.multiply(im, that.im))
    val newIm = arithm.add(arithm.multiply(im, that.re), arithm.multiply(re, that.im))
    Complex(newRe, newIm)
  }

  def /(that: Complex[A]): Complex[A] = {
    val arithm = implicitly[Arithmetic[A]]
    val denom = arithm.add(arithm.multiply(that.re, that.re), arithm.multiply(that.im, that.im))
    val newRe = arithm.add(arithm.multiply(re, that.re), arithm.multiply(im, that.im))
    val newIm = arithm.subtract(arithm.multiply(im, that.re), arithm.multiply(re, that.im))
    Complex(arithm.divide(newRe, denom), arithm.divide(newIm, denom))
  }
}

trait Arithmetic[A] {
  def add(lhs: A, rhs: A): A
  def subtract(lhs: A, rhs: A): A
  def multiply(lhs: A, rhs: A): A
  def divide(lhs: A, rhs: A): A
  def zero: A
}

object Implicits {
  implicit object DoubleArithmetic extends Arithmetic[Double] {
    override def add(lhs: Double, rhs: Double): Double = lhs + rhs
    override def subtract(lhs: Double, rhs: Double): Double = lhs - rhs
    override def multiply(lhs: Double, rhs: Double): Double = lhs * rhs
    override def divide(lhs: Double, rhs: Double): Double = lhs / rhs

    override def zero = 0.0
  }

  implicit object IntArithmetic extends Arithmetic[Int] {
    override def add(lhs: Int, rhs: Int): Int = lhs + rhs
    override def subtract(lhs: Int, rhs: Int): Int = lhs - rhs
    override def multiply(lhs: Int, rhs: Int): Int = lhs * rhs
    override def divide(lhs: Int, rhs: Int): Int = lhs / rhs

    override def zero = 0
  }

  implicit def numberToComplex[A: Arithmetic](number: A): Complex[A] = {
    Complex(number, implicitly[Arithmetic[A]].zero)
  }

  implicit def tuple2ToComplex(t: (Int, Int)): Complex[Int] = {
    Complex(t._1, t._2)
  }

  implicit class ComplexExtension[A: Arithmetic](number: A){
    def imaginary: Complex[A] = Complex(implicitly[Arithmetic[A]].zero, number)
    def real: Complex[A] = Complex(number, implicitly[Arithmetic[A]].zero)
  }
}
