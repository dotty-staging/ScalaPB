package scalapb

import fastparse.ParsingRun
import fastparse.Implicits

object FastParseShims {
  def CharsWhile(p: Char => Boolean)
                (implicit ctx: P[_]): P[Unit] = ???
  def StringIn(s: String*)(implicit ctx: P[_]): P[Unit] = ???
  implicit def LiteralStr(s: String)(implicit ctx: P[Any]): P[Unit] = ???
  def AnyChar(implicit ctx: P[_]): P[Unit] = ???
  def End(implicit ctx: P[_]): P[Unit] = ???
  def &(parse: => P[_])(implicit ctx: P[_]): P[Unit] = ???
  def CharIn(s: String*)(implicit ctx: P[_]): P[Unit] = ???
  def Index(implicit ctx: P[_]): P[Int] = ???
  def P[T](t: P[T])(implicit name: sourcecode.Name, ctx: P[_]): P[T] = ???
  type P[+T] = ParsingRun[T]
  val P = ParsingRun
  type P0 = P[Unit]
  implicit def ByNameOps[T](parse0: => P[T]): ByNameOps[T] = new ByNameOps(() => parse0)
  class ByNameOps[T](val parse0: () => P[T]) extends AnyVal{
    def rep[V](implicit repeater: Implicits.Repeater[T, V],
               whitespace: P[_] => P[Unit],
               ctx: P[Any]): P[V] = ???
    def rep[V](min: Int)
              (implicit repeater: Implicits.Repeater[T, V],
               whitespace: P[_] => P[Unit],
               ctx: P[Any]): P[V] = ???
    def rep[V](min: Int,
               sep: => P[_])
              (implicit repeater: Implicits.Repeater[T, V],
               whitespace: P[_] => P[Unit],
               ctx: P[Any]): P[V] = ???
  }
  implicit def StringToEagerOps[T](str: String)(implicit ctx: P[Any]): EagerOps[Unit] = new EagerOps(LiteralStr(str))
  implicit class EagerOps[T](val parse0: P[T]) extends AnyVal{
    def /(implicit ctx: P[_]): P[T] = ???
    def ~/[V, R](other: P[V])
                (implicit s: Implicits.Sequencer[T, V, R],
                 whitespace: P[Any] => P[Unit],
                 ctx: P[_]): P[R] = ???
    def ~[V, R](other:  P[V])
               (implicit s: Implicits.Sequencer[T, V, R],
                whitespace: P[Any] => P[Unit],
                ctx: P[_]): P[R] = ???
    def ~~/[V, R](other: P[V])
                 (implicit s: Implicits.Sequencer[T, V, R],
                  ctx: P[_]): P[R] = ???
    def ~~[V, R](other: P[V])
                (implicit s: Implicits.Sequencer[T, V, R],
                 ctx: P[_]): P[R] = ???
    def map[V](f: T => V): P[V] = ???
    def filter(f: T => Boolean)
              (implicit ctx: P[Any]): P[T] = ???
    def flatMap[V](f: T => P[V])
                  (implicit whitespace: P[Any] => P[Unit]): P[V] = ???
    def flatMapX[V](f: T => P[V]): P[V] = ???
    def |[V >: T](other: P[V])
                 (implicit ctx: P[Any]): P[V] = ???
    def !(implicit ctx: P[Any]): P[String] = ???
    def ?[V](implicit optioner: Implicits.Optioner[T, V],
             ctx: P[Any]): P[V] = ???
  }
}