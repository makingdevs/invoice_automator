package com.makingdevs

import groovy.servlet.GroovyServlet
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.PathHandler
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.DeploymentInfo
import io.undertow.servlet.api.DeploymentManager
import io.undertow.servlet.api.ServletContainer

/**
 * Created by neodevelop on 15/04/17.
 */
class App {
  static void main(String... args){
    ServletContainer container = io.undertow.servlet.Servlets.defaultContainer()
    DeploymentInfo di = Servlets.deployment()
        .setClassLoader(App.class.getClassLoader())
        .setContextPath("/")
        .setDeploymentName("Invoice Automator")
        .addServlets(Servlets.servlet("groovyServlet", GroovyServlet)
        .addMapping("*.groovy"))
    DeploymentManager manager = container.addDeployment(di); //Add the deployment
    manager.deploy(); // Initial Deployment by servlets not started yet.
    HttpHandler handler = manager.start();
    PathHandler pathHandler = Handlers.path().addPrefixPath("/", handler);
    Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(pathHandler).build()
    server.start()
  }
}
