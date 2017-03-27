package com.makingdevs.routes

import org.apache.camel.builder.RouteBuilder

/**
 * Created by makingdevs on 3/22/17.
 */
class Route2 extends RouteBuilder {

  void configure() {
    from("file:download")
    .to("direct:processOrder")
  }
}
