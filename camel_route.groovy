@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-mail', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-http4', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-zipfile', version='2.18.2')
@Grab(group='ch.qos.logback', module='logback-classic', version='1.2.1')

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.dataformat.zipfile.ZipSplitter
import org.apache.camel.component.mail.MailMessage
import javax.mail.internet.MimeMultipart

def username = System.getenv("USERNAME")
def password = System.getenv("PASSWORD")

def camelContext = new DefaultCamelContext()
camelContext.addRoutes(new RouteBuilder() {
})
//camelContext.addRoutes(new RouteBuilder() {
//  def void configure() {
//    from("file:facturas")
//    .to("file:processed")
//    .log('${body}')
//  }
//})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
