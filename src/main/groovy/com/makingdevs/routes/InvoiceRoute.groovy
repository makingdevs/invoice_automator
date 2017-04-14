package com.makingdevs.routes

import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mail.SplitAttachmentsExpression

import javax.activation.DataHandler

@CompileStatic
class InvoiceRoute extends RouteBuilder {

  void configure() {
    from("direct:processWithAttachments")
    .split(new SplitAttachmentsExpression())
    .process { Exchange exchange ->
      Map<String, DataHandler> attachments = exchange.in.attachments
      String filename = ""
      DataHandler dh = null
      attachments.each { String f, DataHandler h ->
        filename = f
        dh = h
      }

      byte[] data =  exchange.context.typeConverter.convertTo(dh.inputStream.bytes.class, dh.inputStream) as byte[]

      new FileOutputStream(filename).withStream {
        it.write(data)
      }

      exchange.out.setBody(data, data.class)
      Map headers = exchange.in.headers
      headers.put("CamelFileName", filename) // FILENAME!!!!
      exchange.out.setHeaders(headers)
    }
    .to("log:processWitAttachments?showBody=false&showHeaders=true&showFiles=true")
    //.to("direct:storeInLocal")

    from("direct:storeInLocal")
    .recipientList(simple('file:facturas/${header.expeditionYear}/${header.expeditionMonth}/'))
  }

}
