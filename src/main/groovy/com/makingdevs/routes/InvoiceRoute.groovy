package com.makingdevs.routes

import com.makingdevs.routes.utils.ProcessAttachments
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
    .process(new ProcessAttachments())
    //.to("log:processWitAttachments?showBody=false&showHeaders=true&showFiles=true")
    .to("direct:storeInLocal")

    from("direct:storeInLocal")
    .recipientList(simple('file:facturas/${header.expeditionYear}/${header.expeditionMonth}/'))
  }

}
