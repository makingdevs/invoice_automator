package com.makingdevs.web

import com.makingdevs.routes.FilterRoute
import com.makingdevs.routes.InvoiceRoute
import com.makingdevs.routes.UberInvoiceRoute
import com.makingdevs.routes.ZipRoute
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class CamelListener implements ServletContextListener {

  CamelContext camelContext = new DefaultCamelContext()

  void contextInitialized(ServletContextEvent sce) {
    camelContext.addRoutes(new FilterRoute())
    camelContext.addRoutes(new UberInvoiceRoute())
    camelContext.addRoutes(new ZipRoute())
    camelContext.addRoutes(new InvoiceRoute())
    camelContext.start()
  }

  void contextDestroyed(ServletContextEvent sce) {
    camelContext.stop()
  }
}

