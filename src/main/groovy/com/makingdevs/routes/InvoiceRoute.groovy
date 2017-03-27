package com.makingdevs.routes

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder

@CompileStatic
class InvoiceRoute extends RouteBuilder {

	def username = System.getenv("USERNAME")
	def password = System.getenv("PASSWORD")

  void configure() {
    Predicate containsToMail = header("To").contains("fico@makingdevs.com")
    from("imaps://imap.gmail.com?username=${username}"
        + "&password=${password}"
        + "&delete=false&peek=false&unseen=true&consumer.delay=6000&closeFolder=false&disconnect=false")
    .choice()
        .when(containsToMail).to("mock:fico")
        .otherwise().to("mock:unprocessable")
    .end()
    /* TODO: Make the filter
        - fico@makingdevs
        - subject: cfdi factura facturacion invoice
        - pdf and xml
        - zip with files
        - from contains factura facturacion
     */
    .process({ Exchange e ->
      log.info "*"*100
      e.in.headers.each { k, v ->
        log.info "${k} => ${v}"
      }
      log.info "*"*100
      e.in.attachments.each { k, v ->
        log.info "${k} => ${v}"
      }
      log.info "*"*100

    })
    //.to("file:download")
    //.process({ Exchange exchange ->
    //  Message msg = exchange.getIn()
    //  String newMessage = msg.getBody(String).find(/https:\/\/cfdi.uberfacturas.com\/downloadZIP[^"]*/).replace("https:","https4:")
    //  msg.setBody(newMessage)
    //})
    //.toD('${body}?maxRedirects=3')
    //.split(new ZipSplitter()).streaming()
    //.to("file:facturas")
    //.to("log:groovymail?showAll=true&multiline=true&showFiles=true")
  }

}
