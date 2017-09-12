package examples

object lambdaCombinators extends App {

  case class ContactInfo(name: String, email: String)

  def toContactInfoList(
    csv: Seq[String],
    nameRequired: Boolean,
    emailRequired: Boolean
  ): Seq[ContactInfo] =
    csv
      .map(_.split(';'))
      .map { tokens ⇒
        (
          tokens.headOption.getOrElse(""),
          tokens.drop(1).headOption.getOrElse("")
        )
      }
      .flatMap {
        case (name, email) =>
          if ((name == "" && nameRequired) || (email == "" && emailRequired))
            None
          else
            Some(ContactInfo(name, email))
      }

  type Converter = (String, String) ⇒ Option[ContactInfo]

  def toContactInfoList2(csv: Seq[String], converter: Converter): Seq[ContactInfo] =
    csv
      .map(line ⇒ line.split(';'))
      .map { tokens ⇒
        (
          tokens.headOption.getOrElse(""),
          tokens.drop(1).headOption.getOrElse("")
        )
      }
      .flatMap { case (name, email) => converter(name, email) }

  def noEmptyNameOrEmail: Converter = {
    case ("", _) | (_, "") ⇒ None
    case (name, email) ⇒ Some(ContactInfo(name, email))
  }

  // toContactInfoList2(csv, noEmptyNameOrEmail)

  def simplestConverter: Converter = {
    case (name, email) ⇒ Some(ContactInfo(name, email))
  }

  def noEmptyEmail: Converter ⇒ Converter = { converter =>
    {
      case (_, "") ⇒ None
      case (name, email) ⇒ converter(name, email)
    }
  }

  def noEmptyName: Converter ⇒ Converter = converter  ⇒ {
    case ("", _) ⇒ None
    case (name, email) ⇒ converter(name, email)
  }

  val converterChains: Converter = noEmptyEmail(noEmptyName(simplestConverter))

  implicit class ConverterSyntax (convert: Converter) {
    def discardEmptyName = noEmptyName(convert)
    def discardEmptyEmail = noEmptyEmail(convert)
  }

  def converters = simplestConverter.discardEmptyName.discardEmptyEmail

  val csvLines = Seq(
    "test;a@a.com",
    ";b@b.com",
    "b;",
    ""
  )

  val result = toContactInfoList2(csvLines, converters)

  println(result.mkString("\n"))

}
