package com.wunderlist.snowplow.sns

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.snowplowanalytics.snowplow.CollectorPayload.thrift.model1.CollectorPayload
import org.apache.thrift.TDeserializer

object Formats {
  
  val jsonMapper = new ObjectMapper with ScalaObjectMapper { om =>
    om.registerModule(DefaultScalaModule)
  }

  val thriftDeserializer = new TDeserializer

  def parseCollectorPayload(data: Array[Byte]) : CollectorPayload = {
    val payload = new CollectorPayload()
    thriftDeserializer.deserialize(payload, data)
    payload
  }
  
  def parseCollectorPayloadBody(input: String) : Array[PayloadData] = {
    jsonMapper.readValue[Iglu[Array[PayloadData]]](input).data
  }
  
  def parseContext(input: String) :EventContext = {
    jsonMapper.readValue[IgluArray[EventContext]](input).data.head.data
  }

  def parseEvent(input: String) : UnstructuredEvent = {
    val boxedEvent = jsonMapper.readValue[Iglu[Iglu[UnstructuredEvent]]](input)
    return boxedEvent.data.data
  }
  
  def parseApplicationId(input: String): ApplicationId = {
    val bits = input.split(Array('/', ' '))
    if (bits.size == 3) {
      new ApplicationId(bits(0), bits(1), bits(2))
    } else if (bits.size == 2) {
      new ApplicationId(bits(0), bits(1), "")
    } else {
      new ApplicationId(bits(0), "", "")
    }
  }
  
  def valueAsJson(ref: AnyRef) : String = {
    jsonMapper.writeValueAsString(ref)
  }
  
  def platformIdentifier(input: String): String = {
    input match {
      case "ios" => "iOS"
      case "osx" => "Mac"
      case "android" => "Android"
      case _ => input
    }
  }
}