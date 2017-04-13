package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.filters.MailFilter
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.dataformat.zipfile.ZipSplitter

import static org.apache.camel.builder.PredicateBuilder.and
import static org.apache.camel.builder.PredicateBuilder.or

/**
 * Created by makingdevs on 3/22/17.
 */
class ZipRoute extends RouteBuilder {

  void configure(){

    from("direct:processZip")
      .routeId("processZip")
      .split(new ZipSplitter())
        .streaming()
        .to("direct:processWithAttachments")
  }
}
