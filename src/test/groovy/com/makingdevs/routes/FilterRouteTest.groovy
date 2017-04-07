package com.makingdevs.routes

import org.apache.camel.EndpointInject
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.After
import org.junit.Before
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith

/**
 * Created by makingdevs on 3/27/17.
 */
@RunWith(Theories)
class InvoiceRouteTest extends CamelTestSupport {

  public static @DataPoints List<Map<String,String>> candidates = [
      ["Subject":"FACTURA ABRIL"],
      ["Subject":"Fwd: Envio de Factura"],
      ["Subject":"CFDI: MAKING DEVS SC"],
      ["Subject":"FACTURA ELECTRÓNICA JUGUETRON DJ 122113"]
  ]

  @EndpointInject(uri = "mock:directInvoice")
  protected MockEndpoint resultEndpoint

  @Produce(uri = "direct:mail")
  protected ProducerTemplate template

  @Override
  protected RouteBuilder createRouteBuilder() {
    new FilterRoute()
  }

  @Before
  void mockEndpoints(){
    AdviceWithRouteBuilder mockDirect = new AdviceWithRouteBuilder() {
      @Override
      void configure() throws Exception {
        interceptSendToEndpoint("direct:obtainInvoice")
            .skipSendToOriginalEndpoint()
            .to("mock:directInvoice")
      }
    }
    context.getRouteDefinition("filterMessage").adviceWith(context, mockDirect)
    context.start()
  }
  @After
  void stopCamelContext(){ context.stop() }

  @Override
  boolean isUseAdviceWith() { true }

  @Theory
  void testSubjectContainsInvoice(Map headers){
    String value = headers.get("Subject")
    resultEndpoint.expectedHeaderReceived("Subject", value)
    resultEndpoint.expectedMessageCount(1)
    template.sendBodyAndHeaders("direct:mail", "Any body", headers)
    resultEndpoint.assertIsSatisfied()
  }

}
