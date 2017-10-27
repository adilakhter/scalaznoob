package examples

import java.security.{KeyPairGenerator, SecureRandom}
import java.util.Base64

import scalaz._
import Scalaz._
import scala.util.Try

object KeyGenApp extends App {

  val (publicKey, privateKey) = {
    val generator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    generator.initialize(2048, new SecureRandom())
    val pair = generator.generateKeyPair()
    (pair.getPublic, pair.getPrivate)
  }

  println(Base64.getEncoder.encodeToString(privateKey.getEncoded))
  println(Base64.getEncoder.encodeToString(publicKey.getEncoded))
}
