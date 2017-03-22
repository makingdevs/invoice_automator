import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.DEBUG

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender("FILE", FileAppender) {
  file = "invoice_automator.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{15}) - %msg %n"
  }
}
root(DEBUG, ["FILE"])