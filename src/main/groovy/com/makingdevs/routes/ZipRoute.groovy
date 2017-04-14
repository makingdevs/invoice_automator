package com.makingdevs.routes

import com.makingdevs.routes.utils.ProcessAttachments
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.zipfile.ZipSplitter

/**
 * Created by makingdevs on 3/22/17.
 */
class ZipRoute extends RouteBuilder {

  void configure(){

    errorHandler(loggingErrorHandler("com.makingdevs.zip").level(LoggingLevel.ERROR))

    from("direct:attachmentsInZip")
    .process(new ProcessAttachments())
    .to("direct:processZip")

    from("direct:processZip")
      .routeId("processZip")
      .split(new ZipSplitter())
        .streaming()
        .to("direct:storeInLocal")
  }
}
