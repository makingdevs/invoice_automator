package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.filters.MailFilter
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder

import static org.apache.camel.builder.PredicateBuilder.or

/**
 * Created by makingdevs on 3/22/17.
 */
class FilterRoute extends RouteBuilder {

  void configure(){

    Predicate hasCFDISubject = header("Subject").regex(/.*[Cc]fdi|[Cc]FDI.*/)
    Predicate hasInvoiceSubject = header("Subject").regex(/.*([f|F]+actura|[f|F]+ACTURA).*/)
    Predicate isUberInvoice = header("From").contains("uberfacturas.com")
    Predicate attachments = method(MailFilter, "hasAnInvoiceName")

    from(Application.instance.configuration.mail.url)
    .routeId("filterMessage")
    .filter(or(isUberInvoice, hasCFDISubject, hasInvoiceSubject, attachments))
    .process { Exchange e ->
      e.in.headers.each { k, v ->
        println "$k =========>  $v"
      }
      // (factura) OR (facturas) OR (facturacion) OR (facturación) OR (finanzas) OR (fiscal)
      // cfdi
      // zip file
      // pdf and xml
      e
    }
  }
}
