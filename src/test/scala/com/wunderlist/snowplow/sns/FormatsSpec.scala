package com.wunderlist.snowplow.sns

import org.specs2.mutable.Specification

class FormatsSpec extends Specification{
  
  "parsing context json" should {
    lazy val cx = Formats.parseContext("""{"schema":"iglu:com.snowplowanalytics.snowplow\/contexts\/jsonschema\/1-0-0","data":[{"schema":"iglu:com.snowplowanalytics.snowplow\/mobile_context\/jsonschema\/1-0-0","data":{"osType":"osx","openIdfa":"NA","osVersion":"10.10.2","appleIdfv":"NA","carrier":"NA","deviceManufacturer":"Apple Inc.","appleIdfa":"NA","deviceModel":"Desktop"}}]}""")
    
    "parse carier" in {
      cx.carrier shouldEqual "NA"
    }
    
    "parse appleIdfa" in {
      cx.appleIdfa shouldEqual "NA"
    }
    
    "parse deviceModel" in {
      cx.deviceModel shouldEqual "Desktop"
    }
    
    "parse openIdfa" in {
      cx.openIdfa shouldEqual "NA"
    }
    
    "parse deviceManufactuer" in {
      cx.deviceManufacturer shouldEqual "Apple Inc."
    }
    
    "parse osType" in {
      cx.osType shouldEqual "osx"
    }
    
    "parse appleIdfv" in {
      cx.appleIdfv shouldEqual "NA"
    }
    
    "parse osVersion" in {
      cx.osVersion shouldEqual "10.10.2"
    }
  }
  
  "parsing event" should {
    val ueString = """{"schema":"iglu:com.snowplowanalytics.snowplow/unstruct_event/jsonschema/1-0-0","data":{"schema":"iglu:com.wunderlist/client_event/jsonschema/1-0-0","data":{"element":null,"event":"client.application.launch","parameters":{}}}}"""
    lazy val ue = Formats.parseEvent(ueString)
    
    "parse event" in {
      ue.event shouldEqual("client.application.launch")
    }
    
    "parse parameters" in {
      ue.parameters shouldEqual Map()
    }
  }
  
  "parsing event with parameters" should {
    val ueString = """{"schema":"iglu:com.snowplowanalytics.snowplow/unstruct_event/jsonschema/1-0-0","data":{"schema":"iglu:com.wunderlist/client_event/jsonschema/1-0-0","data":{"event":"client.task.create","parameters":{"request_id":"0A20240A-FAD8-4202-BE6C-8B2650DD044E"}}}}"""
    lazy val ue = Formats.parseEvent(ueString)
    
    "parse event" in {
      ue.event shouldEqual("client.task.create")
    }

    "parse parameters" in {
      ue.parameters shouldEqual Map("request_id" -> "0A20240A-FAD8-4202-BE6C-8B2650DD044E")
    }
  }
  
  "parsing three part application identifier" should {
    lazy val appId = Formats.parseApplicationId("Wunderlist/0.0.0 dafs929")
    
    "parse app name" in {
      appId.app shouldEqual("Wunderlist")
    }
    
    "parse app version" in {
      appId.version shouldEqual("0.0.0")
    }
    
    "parse git hash" in {
      appId.gitHash shouldEqual("dafs929")
    }
  }

  "parsing two part application identifier" should {
    lazy val appId = Formats.parseApplicationId("Wunderlist/0.0.0")

    "parse app name" in {
      appId.app shouldEqual("Wunderlist")
    }

    "parse app version" in {
      appId.version shouldEqual("0.0.0")
    }

    "parse git hash" in {
      appId.gitHash shouldEqual("")
    }
  }
  
  "cannonicalizing platform identifier" should {
    "convert ios to iOS" in {
      Formats.platformIdentifier("ios") shouldEqual "iOS"
    }

    "convert osx to Mac" in {
      Formats.platformIdentifier("osx") shouldEqual "Mac"
    }

    "leave garbage alone" in {
      Formats.platformIdentifier("asdf") shouldEqual "asdf"
    }
    
  }
}
