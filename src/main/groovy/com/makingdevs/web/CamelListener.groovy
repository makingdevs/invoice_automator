package com.makingdevs.web

import com.makingdevs.routes.FilterRoute

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext

class CamelListener implements ServletContextListener {

  CamelContext camelContext = new DefaultCamelContext()

  void contextInitialized(ServletContextEvent sce) {
    //camelContext.addRoutes(new InvoiceRoute())
    camelContext.addRoutes(new FilterRoute())
    //camelContext.addRoutes(new UberInvoiceRoute())
    camelContext.start()
  }

  void contextDestroyed(ServletContextEvent sce) {
    camelContext.stop()
  }
}

