package com.makingdevs.routes

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.zipfile.ZipSplitter

/**
 * Created by makingdevs on 3/22/17.
 */
class ZipRoute extends RouteBuilder {

  void configure(){

    from("direct:attachmentsInZip")
    .to("direct:processZip")

    from("direct:processZip")
      .routeId("processZip")
      .split(new ZipSplitter())
        .streaming()
        .to("direct:processWithAttachments")
  }
}
