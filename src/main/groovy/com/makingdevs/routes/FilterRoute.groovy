package com.makingdevs.routes

import com.makingdevs.config.Application
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder

/**
 * Created by makingdevs on 3/22/17.
 */
class FilterRoute extends RouteBuilder {

  void configure(){

    Predicate containsToMail = header("To").contains("fico@makingdevs.com")
    from(Application.instance.configuration.mail.url)
    .filter { Exchange e ->
      // (factura) OR (facturas) OR (facturacion) OR (facturación) OR (finanzas) OR (fiscal)
      // cfdi
      // zip file
      // pdf and xml
      false
    }.to("mock:processed")
  }
}
