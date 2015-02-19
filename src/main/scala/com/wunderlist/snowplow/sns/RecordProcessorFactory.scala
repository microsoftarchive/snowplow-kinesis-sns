package com.wunderlist.snowplow.sns

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory

object RecordProcessorFactory extends IRecordProcessorFactory {
  def createProcessor = new RecordProcessor
}