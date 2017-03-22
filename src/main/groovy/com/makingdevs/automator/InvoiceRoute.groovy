package com.makingdevs.automator

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.zipfile.ZipSplitter

class InvoiceRoute extends RouteBuilder {

	def username = System.getenv("USERNAME")
	def password = System.getenv("PASSWORD")

  def void configure() {
    from("imaps://imap.gmail.com?username=${username}"
        + "&password=${password}"
        + "&delete=false&peek=false&unseen=true&consumer.delay=6000&closeFolder=false&disconnect=false")
    .wireTap("log:originalMessage?showHeaders=true")
    .to("file:download")
    .process({ Exchange exchange ->
      Message msg = exchange.getIn()
      String newMessage = msg.getBody(String).find(/https:\/\/cfdi.uberfacturas.com\/downloadZIP[^"]*/).replace("https:","https4:")
      msg.setBody(newMessage)
    })
    .toD('${body}?maxRedirects=3')
    .split(new ZipSplitter()).streaming()
    .to("file:facturas")
    .to("log:groovymail?showAll=true&multiline=true&showFiles=true")
  }

}
