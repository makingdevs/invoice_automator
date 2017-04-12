package com.makingdevs.routes

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.Message

/**
 * Created by makingdevs on 3/22/17.
 */
@CompileStatic
class UberInvoiceRoute extends RouteBuilder {

  void configure() {
    from("direct:uberInvoice")
    .routeId("uberInvoice")
    .process({ Exchange exchange ->
      Message msg = exchange.getIn()
      String newMessage = msg.getBody(String).find(/https:\/\/cfdi.uberfacturas.com\/downloadZIP[^"]*/).replace("https:","https4:")
      msg.setBody(newMessage)
    })
    .toD('${body}?maxRedirects=3')
    .to("direct:processZip")
  }
}
