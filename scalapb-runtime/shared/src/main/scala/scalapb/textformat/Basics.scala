package scalapb.textformat

import fastparse.NoWhitespace._

import scala.language.implicitConversions

object Basics extends ParserCompat {

  import scalapb.FastParseShims._

  def Newline[$: P] = P(StringIn("\r\n", "\n"))

  case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T) = f(t)

    override def toString() = name
  }

  private val hexDigitStr = "0123456789abcdefABCDEF"

  val Digits    = NamedFunction('0' to '9' contains (_: Char), "Digits")
  val HexDigits = NamedFunction(hexDigitStr contains (_: Char), "HexDigits")
  val OctDigits = NamedFunction('0' to '7' contains (_: Char), "OctDigits")
  val CharChunk = NamedFunction((c: Char) => !"\n\r".contains(c), "CharChunk")

  // TODO(nadavsr): figure out this
  def sameLineCharChunks[$: P] = P(CharsWhile(CharChunk) | (!Newline) ~ AnyChar)

  def lineComment[$: P] = P("#" ~ sameLineCharChunks.rep ~ &(Newline | End))

  def whiteSpace[$: P] = (CharIn(" \n\r\t\f") | lineComment).`opaque`("whitespace").rep

  def identifier[$: P] = P(CharIn("a-z", "A-Z", "0-9", "_").rep(1).!).`opaque`("identifier")

  def literal[$: P] = P(CharIn("a-z", "A-Z", "0-9", "_\\-.").rep(1).!).`opaque`("literal")

  def digits[$: P]    = P(CharsWhile(Digits))
  def hexDigits[$: P] = P(CharsWhile(HexDigits))
  def octDigits[$: P] = P(CharsWhile(OctDigits))

  def exponent[$: P] = P(CharIn("eE") ~ CharIn("+\\-").? ~ digits)
  def fractional[$: P] =
    (CharIn("+\\-").? ~ (digits ~ "." ~ digits.? | "." ~ digits) ~ exponent.? ~ CharIn("fF").?).!

  def decIntegral[$: P] = P("0" | CharIn("1-9") ~ digits.?).!.map(p => BigInt(p))
  def hexIntegral[$: P] = P("0x" ~/ hexDigits.!).map(p => BigInt(p, 16))
  def octIntegral[$: P] = P("0" ~ octDigits.!).map(p => BigInt(p, 8))

  def integral[$: P]: P[BigInt] = P(hexIntegral | octIntegral | decIntegral)

  def bigInt[$: P]: P[BigInt] =
    P(CharIn("+\\-").!.? ~ integral).map({
      case (Some("-"), number) => -number
      case (_, number)         => number
    })

  def strNoDQChars[$: P] = P(CharsWhile(!"\"\n\\".contains(_: Char)))
  def strNoQChars[$: P]  = P(CharsWhile(!"'\n\\".contains(_: Char)))
  def escape[$: P]       = P("\\" ~ AnyChar)
  def singleBytesLiteral[$: P] =
    P(
      "\"" ~/ (strNoDQChars | escape).rep.! ~ "\"" |
        "'" ~/ (strNoQChars | escape).rep.! ~ "'"
    ).`opaque`("string")

  def bytesLiteral[$: P] = P(singleBytesLiteral.rep(1, whiteSpace)).map(_.mkString)

  def boolean[$: P]: P[Boolean] =
    P(
      ("true" | "t" | "1").map(_ => true) |
        ("false" | "f" | "0").map(_ => false)
    ).`opaque`("'true' or 'false'")

  def ws[$: P](s: String): P[Unit] = P(s ~ &(whiteSpace))
}