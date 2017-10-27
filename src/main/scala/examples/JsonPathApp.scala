package examples

import java.util

import com.jayway.jsonpath.{ Configuration, DocumentContext, JsonPath }
import java.util.{ List => JList }

import com.jayway.jsonpath.{ Option => JOption }
import examples.JsonPathApp.{ configuration, json }

import scalaz._
import Scalaz._
import net.minidev.json.JSONArray

object JsonFunctionExpressions {

  import Disjunction._
  import scala.collection.JavaConverters._

  val isDefinitePath: String => Boolean    = JsonPath.isPathDefinite
  val returnsJsonObject: Object => Boolean = _.isInstanceOf[util.Map[_, _]]
  val returnsNull: Object => Boolean       = _ == null

  val returnsJsonArray: Object => Boolean = _.isInstanceOf[JSONArray]
  val returnsJsonValue: Object => Boolean = obj => !(returnsNull(obj) || returnsJsonObject(obj) || returnsJsonArray(obj))
  val returnsJsonOrMap: Object => Boolean = obj => !(returnsNull(obj) || returnsJsonArray(obj))

  type QueryPath  = String
  type QueryAlias = String
  type Result[T]  = Disjunction[String, T]

  val processRawJsonValue: (QueryAlias, Object) => String = {
    case (_, obj) => obj.toString
  }

  val processRawJsonObject: (QueryAlias, Object) => collection.Map[String, String] = {
    case (qa, obj) =>
      if (returnsJsonObject(obj)) {
        obj.asInstanceOf[util.LinkedHashMap[String, Object]].asScala.map { case (key, value) => (s"$qa.$key", value.toString) }
      } else collection.Map(qa -> obj.toString)
  }

  private def validateAndTransformResult[T](obj: Object, queryAlias: QueryAlias, isValidResult: Object => Boolean)(transform: (QueryAlias, Object) => T): Result[T] =
    isValidResult(obj)
      .either(transform(queryAlias, obj))
      .or(s"Simple Json Value expected. Found: `${returnsNull(obj) ? "null" | obj.getClass.toString}`")

  private def performQuery(docContext: DocumentContext, queryString: String): Object =
    docContext.read(queryString, classOf[Object])

  private def validateQueryPath(jsonPath: QueryPath): Result[QueryPath] =
    isDefinitePath(jsonPath)
      .either(jsonPath)
      .or(s"Path `$jsonPath` is not a Definite path. Note: Only Definite path is supported.")

  private def processQueryPath(queryPath: QueryPath): Result[(QueryAlias, QueryPath)] = {
    fromTryCatchNonFatal {
      val aliasAndQueryPath: Array[QueryPath] = queryPath.trim.split("::")
      if (aliasAndQueryPath.length <= 1)
        (queryPath, queryPath)
      else
        (aliasAndQueryPath(0), aliasAndQueryPath(1))
    }.leftMap(_.getMessage)
  }

  def extractJson[T](queryPath: QueryPath, isValidResult: Object => Boolean, processResult: (QueryAlias, Object) => T)(implicit docContext: DocumentContext): Result[T] = {
    val result =
      for {
        queryPath       <- validateQueryPath(queryPath)
        (alias, qPath)  <- processQueryPath(queryPath)
        rawResult       <- fromTryCatchNonFatal(performQuery(docContext, qPath)).leftMap(_.getMessage)
        processedResult <- validateAndTransformResult(rawResult, alias, isValidResult)(processResult)
      } yield processedResult

    result.leftMap(error => {
      println(s"ej|----$queryPath :  $error"); error
    })
  }

  def extractJsonValue3(queryString: QueryPath)(implicit docContext: DocumentContext): Result[String] = {
    val f: (QueryAlias, Object) => String = { case (_, obj) => obj.toString }
    extractJson(queryString, returnsJsonValue, f)
  }

  def extractJsonValue2(queryString: QueryPath)(implicit docContext: DocumentContext): Result[String] =
    for {
      queryPath      <- validateQueryPath(queryString)
      (alias, qPath) <- processQueryPath(queryPath)
      queryResult    <- fromTryCatchNonFatal(performQuery(docContext, qPath)).leftMap(_.getMessage)
      convertedResult <- validateAndTransformResult(queryResult, alias, returnsJsonValue) { (a, obj) =>
        obj.toString
      }
    } yield convertedResult

  def extractJsonMap(qPath: QueryPath)(implicit docContext: DocumentContext): collection.Map[String, String] = {
    import scala.collection.JavaConverters._
    val result: Disjunction[String, collection.Map[QueryAlias, QueryPath]] =
      for {
        queryPath      <- validateQueryPath(qPath)
        (alias, qPath) <- processQueryPath(queryPath)
        queryResult    <- fromTryCatchNonFatal(performQuery(docContext, qPath)).leftMap(_.getMessage)
        convertedResult <- validateAndTransformResult(queryResult, alias, returnsJsonOrMap) { (qa, obj) =>
          if (returnsJsonObject(obj)) {
            obj
              .asInstanceOf[util.LinkedHashMap[String, Object]]
              .asScala
              .map {
                case (key, value) =>
                  (s"$qa.$key", value.toString)
              }
          } else {
            collection.Map(qa -> obj.toString)
          }
        }
      } yield convertedResult

    result.leftMap(error => println(s"|----$qPath :  $error"))
    result.fold(_ => collection.Map.empty[String, String], identity)
  }

  implicit class ExtractFromJsonOps(jsonString: String) {

    def extractJsonValue(queryString: String): Option[String] = {
      implicit val jsonContext: DocumentContext = JsonPath.parse(jsonString)
      extractJson(queryString, returnsJsonValue, processRawJsonValue).toOption
    }

    def extractMap(queryString: QueryPath*): Map[String, String] = {
      implicit val jsonContext: DocumentContext = JsonPath.parse(jsonString)
      queryString
        .map(
          extractJson(_, returnsJsonOrMap, processRawJsonObject)
            .fold(_ => collection.Map.empty[String, String], identity))
        .reduce(_ ++ _)
        .toMap
    }
  }
}

object JsonPathApp extends App {

  private def readStuff(index: Int, queryString: String, docContext: DocumentContext): String = {
    println(s"Processing $queryString")
    val value3: Object = docContext.read(queryString, classOf[Object])
    s"[$index]: $queryString -> ${JsonPath.isPathDefinite(queryString)} -> ${value3.getClass} \t ${value3.toString}"
  }

  val json =
    """
      |{
      |    "store": {
      |        "book": [
      |            {
      |                "category": "reference",
      |                "author": "Nigel Rees",
      |                "title": "Sayings of the Century",
      |                "price": 8.95
      |            },
      |            {
      |                "category": "fiction",
      |                "author": "Evelyn Waugh",
      |                "title": "Sword of Honour",
      |                "price": 12.99
      |            },
      |            {
      |                "category": "fiction",
      |                "author": "Herman Melville",
      |                "title": "Moby Dick",
      |                "isbn": "0-553-21311-3",
      |                "price": 8.99
      |            },
      |            {
      |                "category": "fiction",
      |                "author": "J. R. R. Tolkien",
      |                "title": "The Lord of the Rings",
      |                "isbn": "0-395-19395-8",
      |                "price": 22.99
      |            }
      |        ],
      |        "bicycle": {
      |            "color": "red",
      |            "price": 19.95
      |        },
      |        "numbers" : [1, 2, 3, 4]
      |    },
      |    "expensive": 10
      |}
  """.stripMargin

  private val configuration = Configuration.defaultConfiguration()
  //.addOptions(Option.ALWAYS_RETURN_LIST)
  private val jsonContext: DocumentContext = JsonPath.using(configuration).parse(json)

  val queries =
    List(
      "$.store.book[*].author",
      "$.store.book[*].price",
      "$.store.book[?(@.price > 10)]",
      "$.store.book[0]",
      "$.store.bicycle.price",
      "$.store.book[*].author",
      "$..author",
      "$.store..price",
      "$..book[?(@.isbn)]",
      "$..book.length()",
      "$..book[-2:]",
      "alias::$.store.book[0].title",
      "$.store.bicycle[?(@.color == 'red' )]",
      "$.store.bicycle",
      "$.store.numbers.min()"
    )

  import JsonFunctionExpressions._

  val results2 =
    queries.zipWithIndex
      .map {
        case (qs, idx) =>
          s"$idx : $qs : ${json.extractJsonValue(qs)}"
      }
      .mkString("\n")

  implicit val jsContext: DocumentContext = jsonContext
  extractJsonValue2("$.store.book[0].title") |> println
  extractJsonValue2("$.unkown") |> println

  extractJsonMap("$.store.book[0].title") |> println
  extractJsonMap("a::$.store.book[0].title") |> println
  extractJsonMap("a::$.store.book[0]") |> println

  extractJsonValue3("$.store.book[0].title") |> println

  json.extractMap("b::$.store.bicycle", "a::$.store.book[0]", "$.store.book[0].title", "$.unknown").mkString("\n") |> println
}
