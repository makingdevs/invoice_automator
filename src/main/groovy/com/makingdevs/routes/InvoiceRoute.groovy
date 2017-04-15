package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.utils.ProcessAttachments
import groovy.transform.CompileStatic
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mail.SplitAttachmentsExpression

@CompileStatic
class InvoiceRoute extends RouteBuilder {

  ConfigObject configuration = Application.instance.configuration

  String localPath = configuration.get("file")["localPath"]

  String fileEndpoint = "file:${localPath}/\${header.expeditionYear}/\${header.expeditionMonth}/"

  void configure() {

    errorHandler(loggingErrorHandler("com.makingdevs.invoice").level(LoggingLevel.ERROR))

    from("direct:processWithAttachments")
        .split(new SplitAttachmentsExpression())
        .process(new ProcessAttachments())
        .to("direct:storeInLocal")

    from("direct:storeInLocal")
        .recipientList(simple(fileEndpoint))
  }

}
