package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.filters.MailFilter
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder

import static org.apache.camel.builder.PredicateBuilder.and
import static org.apache.camel.builder.PredicateBuilder.or

/**
 * Created by makingdevs on 3/22/17.
 */
class FilterRoute extends RouteBuilder {

  void configure(){

    Predicate hasCFDISubject = header("Subject").regex(/.*[Cc]fdi|[Cc]FDI.*/)
    Predicate hasInvoiceSubject = header("Subject").regex(/.*([f|F]+actura|[f|F]+ACTURA).*/)
    Predicate isUberInvoice = header("From").contains("uberfacturas.com")
    Predicate attachments = method(MailFilter, "hasFilesFromAnInvoice")
    Predicate zipFile = method(MailFilter, "hasZipFile")

    from(Application.instance.configuration.mail.url)
    .routeId("filterMessage")
    .choice()
    .when(and(or(hasCFDISubject,hasInvoiceSubject), attachments)).to("log:unzip")
    .when(and(or(hasCFDISubject,hasInvoiceSubject), zipFile)).to("log:zipfile")
    .when(isUberInvoice).to("log:uber")
    .otherwise().to("log:unprocessable")
    .end()
  }
}
