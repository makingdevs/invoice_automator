package com.makingdevs.automator

import org.apache.camel.builder.RouteBuilder

/**
 * Created by makingdevs on 3/22/17.
 */
class Route1 extends RouteBuilder {

  void configure(){
    from("direct:processOrder")
    .to("file:processed")
  }
}
