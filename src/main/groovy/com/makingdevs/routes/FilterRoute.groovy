package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.filters.MailFilter
import org.apache.camel.Exchange
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
        .setHeader("expeditionMonth", method(MailFilter, "extractMonthInvoice"))
        .setHeader("expeditionYear", method(MailFilter, "extractYearInvoice"))
        .choice()
          .when(and(or(hasCFDISubject,hasInvoiceSubject), attachments))
            .to("direct:processWithAttachments")
          .when(and(or(hasCFDISubject,hasInvoiceSubject), zipFile))
            .to("direct:processZip")
          .when(isUberInvoice)
            .to("direct:uberInvoice")
          .otherwise()
            .to("log:unprocessable")
        .end()
  }
}
