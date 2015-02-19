package com.wunderlist.snowplow.sns

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
case class Iglu[T](schema: String, data: T)

@JsonIgnoreProperties(ignoreUnknown = true)
case class PayloadData(aid: String,
                       res: String,
                       uid: String,
                       p: String,
                       cx: String,
                       dtm: String,
                       tv: String,
                       tna: String,
                       ue_px: String,
                       e: String,
                       lang: String,
                       vp: String,
                       eid: String)

case class IgluArray[T](schema: String, data: Array[Iglu[T]])

@JsonIgnoreProperties(ignoreUnknown = true)
case class EventContext(carrier: String,
                        appleIdfa: String,
                        openIdfa: String,
                        appleIdfv: String,
                        deviceModel: String,
                        deviceManufacturer: String,
                        osType: String,
                        osVersion: String)

@JsonIgnoreProperties(ignoreUnknown = true)
case class UnstructuredEvent(product_version: String,
                             git_hash: String,
                             event: String,
                             parameters: Map[String,String])

case class ApplicationId(app: String, version: String, gitHash: String)