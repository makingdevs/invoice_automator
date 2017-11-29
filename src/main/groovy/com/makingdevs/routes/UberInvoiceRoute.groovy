package com.makingdevs.routes

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.Message
import org.apache.camel.builder.RouteBuilder

import java.util.regex.Matcher

/**
 * Created by makingdevs on 3/22/17.
 */
@CompileStatic
class UberInvoiceRoute extends RouteBuilder {

  void configure() {

    errorHandler(loggingErrorHandler("com.makingdevs.uber").level(LoggingLevel.ERROR))

    from("direct:uberInvoice")
    .routeId("uberInvoice")
    .process({ Exchange exchange ->
      String msg = exchange.in.getBody(String)
      String regex = /{1}http:\/\/email.uber.com\/.*{1}(?=\>)/
      Matcher matcher = msg =~ regex
      if(matcher.size() > 0){
        exchange.out.setBody(matcher[0..1])
      }else{
        println "there are not links to download files"
      }
    }).split(body())
    .process({ Exchange exchange ->
      String link = exchange.in.getBody(String)
      link = link.replace("http:", "https:")
      exchange.out.setBody(link)
      println "<<<<<<<<<<<<<<<"
    })
    .toD('${body}') 
    //.to("direct:processZip")
  }
}
