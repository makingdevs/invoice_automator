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
    .to("log:com.makingdevs.errorz?level=ERROR&multiline=true&maxChars=1000000")
    .process({ Exchange exchange ->
      String msg = exchange.in.getBody(String)
      String regex = /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.*/
      Matcher matcher = msg =~ regex
      String infoError
      if(matcher.size() > 0){
        infoError = matcher[0]
      }
      String message = """\
        No se puede procesar esta factura.
        No se encontraron los links
        Correo Info: ${infoError ?: 'No data'}
      """.toString()
      exchange.out.setBody(message, String)
    })
    .handled(true)
    .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")

    onException(RemovedLinkException)
    .to("log:com.makingdevs.errorz?level=ERROR&multiline=true&maxChars=1000000")
    .process({ Exchange exchange ->
      String msg = exchange.in.getBody(String)
      String regex = /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.*/
      Matcher matcher = msg =~ regex
      String infoError
      if(matcher.size() > 0){
        infoError = matcher[0]
      }q
      String message = """\
        No se puede procesar esta factura.
        Los links regresan un codigo de status diferente a 200
        Correo Info: ${infoError ?: 'No data'}
      """.toString()
      exchange.out.setBody(message, String)
    })
    .handled(true)
    .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")

    from("direct:uberInvoice")
    .routeId("uberInvoice")
    .process({ Exchange exchange ->
      String msg = exchange.in.getBody(String)
      String regex = /(https?:\/\/click.uber.com\/f\/a\/.*{1}(?=">)|https?:\/\/email.uber.com\/wf\/click.*{1}(?=">))/
      Matcher matcher = msg =~ regex
      if(matcher.size() > 0){
        ArrayList matchGroup1 = (ArrayList)matcher[0]
        ArrayList matchGroup2 = (ArrayList)matcher[1]
        exchange.out.setHeaders(exchange.in.headers)
        exchange.out.setBody([matchGroup1[0], matchGroup2[0]])
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
