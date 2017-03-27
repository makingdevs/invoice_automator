package com.makingdevs.routes

import com.makingdevs.config.Application
import org.apache.camel.builder.RouteBuilder

/**
 * Created by makingdevs on 3/22/17.
 */
class Route1 extends RouteBuilder {

  void configure(){
    from(Application.instance.configuration.test.mail.url)
    .to("mock:processed")
  }
}
