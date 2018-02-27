package com.makingdevs.routes

import com.makingdevs.routes.utils.ProcessInvoiceDetail
import com.makingdevs.routes.utils.ProcessLinkUber
import com.makingdevs.config.Application
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import java.util.ArrayList
import java.util.regex.Matcher
import com.makingdevs.exception.NoLinkException
import com.makingdevs.exception.RemovedLinkException

/**
 * Created by makingdevs on 3/22/17.
 */
@CompileStatic
class UberInvoiceRoute extends RouteBuilder {

  ConfigObject configuration = Application.instance.configuration

  void configure() {

    onException(NoLinkException)
    .process({ Exchange exchange ->
      String message = """\
        No se puede procesar esta factura.
        No se encontraron los links
      """.toString()
      exchange.out.setBody(message, String)
    })
    .handled(true)
    .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")

    onException(RemovedLinkException)
    .process({ Exchange exchange ->
      String message = """\
        No se puede procesar esta factura.
        Los links regresan un codigo de status diferente a 200
      """.toString()
      exchange.out.setBody(message, String)
    })
    .handled(true)
    .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")

    from("direct:uberInvoice")
    .routeId("uberInvoice")
    .process({ Exchange exchange ->
      String msg = exchange.in.getBody(String)
      String regex = /https?:\/\/email.uber.com\/wf\/click.*{1}(?=\>)/
      Matcher matcher = msg =~ regex
      if(matcher.size() > 0){
        exchange.out.setHeaders(exchange.in.headers)
        exchange.out.setBody(matcher[0..1])
      }else{
        throw new NoLinkException("There are not links to download files")
      }
    }).split(body())
    .process(new ProcessLinkUber())
    .process(new ProcessInvoiceDetail())
    .to("direct:storeInLocal")
    //.to("direct:processZip")
  }
}
