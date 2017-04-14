package com.makingdevs.routes

import com.makingdevs.routes.utils.ProcessAttachments
import groovy.transform.CompileStatic
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mail.SplitAttachmentsExpression

@CompileStatic
class InvoiceRoute extends RouteBuilder {

  void configure() {
    from("direct:processWithAttachments")
    .split(new SplitAttachmentsExpression())
    .process(new ProcessAttachments())
    .to("direct:storeInLocal")

    from("direct:storeInLocal")
    .recipientList(simple('file:facturas/${header.expeditionYear}/${header.expeditionMonth}/'))
  }

}
