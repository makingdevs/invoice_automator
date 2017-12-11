package com.makingdevs.routes

import com.makingdevs.routes.utils.ProcessInvoiceDetail
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import java.util.ArrayList
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
        exchange.out.setHeaders(exchange.in.headers)
        exchange.out.setBody(matcher[0..1])
      }else{
        println "there are not links to download files"
      }
    }).split(body())
    .process({ Exchange exchange ->
      String link = exchange.in.getBody(String)
      link = link.replace("http:", "http4:")
      exchange.out.setHeaders(exchange.in.headers)
      exchange.out.setBody(link)
    })
    .toD('${body}') 
    .process({ Exchange exchange ->
      byte[] data = exchange.in.getBody(byte[])
      exchange.out.setBody(data, data.class)
      
      Map headers = exchange.in.headers
      String regex = /"(.*?)"/
      Matcher matcher = headers['Content-Disposition'] =~ regex
      if(matcher.size() > 0){ 
        headers.put("CamelFileName", ((ArrayList)matcher[0]).get(1)) // FILENAME!!!!
        exchange.out.setHeaders(headers)
      }else{
        println "no match"
      }
    })
    .process(new ProcessInvoiceDetail())
    .to("direct:storeInLocal")
    //.to("direct:processZip")
  }
}
