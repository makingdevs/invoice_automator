package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.filters.MailFilter
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.PredicateBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mail.MailHeaderFilterStrategy

import static org.apache.camel.builder.PredicateBuilder.*

/**
 * Created by makingdevs on 3/22/17.
 */
class FilterRoute extends RouteBuilder {

  void configure(){

    Predicate hasCFDISubject = PredicateBuilder.regex(header("Subject"), /.*[Cc]fdi|[Cc]FDI.*/)
    Predicate hasInvoiceSubject = PredicateBuilder.regex(header("Subject"), /.*[fF]actura|[fF]ACTURA.*/)
    Predicate isUberInvoice = header("From").contains("uberfacturas.com")

    from(Application.instance.configuration.mail.url)
    .routeId("filterMessage")
    .filter(or(isUberInvoice, hasCFDISubject, hasInvoiceSubject, method(MailFilter, "hasAnInvoiceName") ))
    .to("direct:obtainInvoice")
    //.filter { Exchange e ->
    //  e.in.headers.each { k, v ->
    //    println "$k =========>  $v"
    //  }
    //  // (factura) OR (facturas) OR (facturacion) OR (facturación) OR (finanzas) OR (fiscal)
    //  // cfdi
    //  // zip file
    //  // pdf and xml
    //  true
    //}.to("direct:obtainInvoice")
  }
}
