package com.makingdevs.routes

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder

import javax.activation.DataHandler

@CompileStatic
class InvoiceRoute extends RouteBuilder {

  void configure() {
    from("direct:processWithAttachments")
    .to("log:processWithAttachments?showHeaders=true")
  }

}
