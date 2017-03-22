package com.makingdevs.web

import com.makingdevs.automator.Route1
import com.makingdevs.automator.Route2

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import com.makingdevs.automator.InvoiceRoute

class CamelListener implements ServletContextListener {

  CamelContext camelContext = new DefaultCamelContext()

  void contextInitialized(ServletContextEvent sce) {
    camelContext.addRoutes(new InvoiceRoute())
    camelContext.addRoutes(new Route1())
    camelContext.addRoutes(new Route2())
    camelContext.start()
  }

  void contextDestroyed(ServletContextEvent sce) {
    camelContext.stop()
  }
}

