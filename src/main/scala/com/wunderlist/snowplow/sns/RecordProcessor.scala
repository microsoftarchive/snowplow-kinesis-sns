package com.wunderlist.snowplow.sns

import java.lang.Exception

import com.amazonaws.services.kinesis.clientlibrary.interfaces.{IRecordProcessorCheckpointer, IRecordProcessor}
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason
import com.amazonaws.services.kinesis.model.Record

import com.snowplowanalytics.snowplow.CollectorPayload.thrift.model1.CollectorPayload
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import sun.misc.BASE64Decoder

import scala.collection.JavaConversions._


class RecordProcessor extends IRecordProcessor {

  var shardId : String = ""
  val iso8601formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(DateTimeZone.UTC)

  def initialize(givenShardId: String): Unit = {
    shardId = givenShardId
    println(s"Record processor connected to $shardId")
  }

  def processRecords(records: java.util.List[Record], checkpointer: IRecordProcessorCheckpointer) : Unit = {
    val recordCount = records.size
    println(s"Processing $recordCount records from $shardId")
    // TODO mutithread this shiznit....
    for (record <- records) {
      processRecord(record)
    }
    checkpointer.checkpoint()
    println(s"Checkpointed $recordCount records on $shardId")
  }

  def processRecord(record: Record) : Unit = {
    val collecterPayload = Formats.parseCollectorPayload(record.getData.array())
    val body = collecterPayload.getBody
    if (body != null) {
      for (payloadData <- Formats.parseCollectorPayloadBody(body)) {
        try {
          processPayloadEvent(payloadData, collecterPayload)
        } catch {
          case e: RuntimeException => {
            println("Skipping record " + record + " due to " + e)
          }
        }
      }
    }
  }
  
  def processPayloadEvent(data: PayloadData, payload: CollectorPayload) = {
    val cx = new String(new BASE64Decoder().decodeBuffer(data.cx))
    
    val ue = new String(new BASE64Decoder().decodeBuffer(data.ue_px))

    println(s"CX $cx")
    println(s"UE $ue")

    val context = Formats.parseContext(cx)
    val event = Formats.parseEvent(ue)
    
    println(s"CONTEXT: $context")
    println(s"EVENT: $event")
    
    if (context != null && event != null) {
      val appId = Formats.parseApplicationId(data.aid)
      var params = event.parameters;
      if (params == null) {
        params = Map()
      }
      var eventName = event.event
      if (eventName == null) {
        eventName = ""
      }

      val msg = Formats.valueAsJson(Map(
        "event" -> eventName,
        "parameters" -> event.parameters,
        "source" -> "snowplow-kinesis-sns",
        "date" ->  iso8601formatter.print(payload.timestamp),
        "ip" -> payload.ipAddress,
        "platform" -> Formats.platformIdentifier(context.osType),
        "git_hash" -> appId.gitHash,
        "product_version" -> appId.version,
        "system_version" -> context.osVersion,
        "device" -> context.deviceModel,
        "user_id" -> data.uid,
        "unregistered_user_id" -> payload.networkUserId
      ))
      
      println(s"PUBLISHING: $msg")

      Application.snsClient.publishEvent(msg)
    } else {
      println(s"Skipped reporting for payload: $payload")
    }
  }

  def parseVersionFromUserAgent(input: String): Option[String] = {
    "/(.*?) ".r.findFirstMatchIn(input).map { v => v.group(1) }
  }
  
  def shutdown(checkpointer: IRecordProcessorCheckpointer, reason: ShutdownReason) : Unit = {
    println(s"Record processor disconnected from $shardId")
  }
}
