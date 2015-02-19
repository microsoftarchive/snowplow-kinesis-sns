package com.wunderlist.snowplow.sns

import java.util.UUID
import com.amazonaws.auth.{InstanceProfileCredentialsProvider, BasicAWSCredentials, AWSCredentialsProvider, AWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{Worker, InitialPositionInStream, KinesisClientLibConfiguration}
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.{PublishRequest}


object Application extends App {

  var exit = false
  
  val applicationName = sys.env.get("APP_NAME") 
  applicationName match {
    case Some(name) => {
      println(s"Registering Kinesis App as $name")
    }
    case None => {
      println("APP_NAME environment variable must be defined ")
      exit = true
    }
  }

  val streamName = sys.env.get("STREAM_NAME")
  streamName match {
    case Some(name) => {
      println(s"Pulling Kineses events from $name")
    }
    case None => {
      println("STREAM_NAME environment variable must be defined ")
      exit = true
    }
  }

  val topicArn = sys.env.get("SNS_TOPIC") 
  topicArn match {
    case Some(arn) => {
      println(s"Sending SNS notifications to $arn")
    }
    case None => {
      println("SNS_TOPIC environment variable must be defined ")
      exit = true
    }
  }
  
  val regionName = sys.env.getOrElse("AWS_REGION", Regions.EU_WEST_1.getName)

  if (exit) { sys.exit(-1) }

  var credentialsProvider : AWSCredentialsProvider = null

  if (System.getenv.containsKey("AWS_ACCESS_KEY_ID")) {
    println("Using local AWS credentials")
    val key = System.getenv("AWS_ACCESS_KEY_ID")
    val secret = System.getenv("AWS_SECRET_ACCESS_KEY")
    val credentials = new BasicAWSCredentials(key, secret)
    credentialsProvider = new AWSCredentialsProvider {
      override def refresh(): Unit = {}
      override def getCredentials: AWSCredentials = credentials
    }
  } else {
    println("Using IAM credentials")
    credentialsProvider = new InstanceProfileCredentialsProvider
  }

  val snsClient = new AmazonSNSClient(credentialsProvider) {
    setRegion(Regions.EU_WEST_1)

    def publishEvent(eventMsg: String) : Unit = {
      val publishRequest = new PublishRequest(topicArn.get, eventMsg)
      publishRequest.setSubject("Snowplow event")
      val publishResult = publish(publishRequest)
    }
  }

  val workerId = UUID.randomUUID().toString
  val initialPositionInStream = InitialPositionInStream.TRIM_HORIZON

  val config = new KinesisClientLibConfiguration(applicationName.get, streamName.get, credentialsProvider, workerId)
    .withInitialPositionInStream(initialPositionInStream)
    .withRegionName(regionName)

  val worker = new Worker(RecordProcessorFactory, config)

  worker.run

}
