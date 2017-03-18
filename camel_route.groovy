@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-mail', version='2.18.2')
@Grab(group='ch.qos.logback', module='logback-classic', version='1.2.1')

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.component.mail.MailMessage
import javax.mail.internet.MimeMultipart

class MyBean {
  void doSomething(MimeMultipart mailMessage, Exchange exchange) {
    println "*"*80
    println "ATTACHMENTS SIZE : ${exchange.in.attachments.size()}"
    println "ATTACHMENTS : ${exchange.in.attachments}"
    println "*"*200
    def body = exchange.getIn().getBody(String)
    println body
    new File("salida.txt").write(body)
    println "*"*200
    println mailMessage.parts*.message*.bs*.dump()
    println "-"*80
  }
}

def username = "username"
def password = "password"

def camelContext = new DefaultCamelContext()
camelContext.addRoutes(new RouteBuilder() {
  def void configure() {
  from("imaps://imap.gmail.com?username=${username}"
      + "&password=${password}"
      + "&delete=false&peek=false&unseen=true&consumer.delay=6000&closeFolder=false&disconnect=false")
  //.filter {it.in.headers.subject.contains('factura')}
  .to("file:download")
  .bean(MyBean, "doSomething")
  .to("log:groovymail?showAll=true&multiline=true&showFiles=true")
  }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
println "Camel started!!!"
