import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

String env = System.getenv("ENVIRONMENT") ?: "development"

appender("FILE", FileAppender) {
  file = "invoice_automator.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{15}) - %msg %n"
  }
}

level = DEBUG

if(env == "production" )
  level = ERROR

root(level, ["FILE"])
