# snowplow-kinesis-sns

Wunderlist’s Kinesis to SNS bridge for Snowplow events. This bridge takes events collected by the Snowplow Scala collector and published to a Kinesis stream and builds a new event that is put into a queue to be processed by our existing analytics infrastructure. 
