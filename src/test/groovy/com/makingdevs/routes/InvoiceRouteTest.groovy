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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by makingdevs on 3/27/17.
 */
@RunWith(Parameterized)
class InvoiceRouteTest extends CamelTestSupport {

  @Parameterized.Parameter(0)
  public Map headers

  @Parameterized.Parameter(1)
  public int processWithAttachments

  @Parameterized.Parameter(2)
  public int processZip

  @Parameterized.Parameter(3)
  public uberInvoice

  @Parameterized.Parameters(name = "Msg with headers {0}, {1}")
  static data(){
    [
      [["Subject":"Envío de cfdi"],0,0,0],
      [["Subject":"FACTURA ABRIL"],0,0,0],
      [["Subject":"CFDI: MAKING DEVS SC", "From":"cfdi@uberfacturas.com"],0,0,0],
      [["Subject":"Fwd: Envio de Factura"],0,0,0],
      [["Subject":"CFDI: MAKING DEVS SC"],0,0,0],
      [["Subject":"FACTURA ELECTRÓNICA JUGUETRON DJ 122113"],0,0,0],
      [["Subject":"Factura Marzo Chihuahua230"],0,0,0]
    ]*.toArray()
  }

  @EndpointInject(uri = "mock:processWithAttachments")
  protected MockEndpoint attachmentsEndpoint

  @EndpointInject(uri = "mock:processZip")
  protected MockEndpoint zipEndpoint

  @EndpointInject(uri = "mock:uberInvoice")
  protected MockEndpoint uberEndpoint

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
        interceptSendToEndpoint("direct:processWithAttachments")
            .skipSendToOriginalEndpoint()
            .to("mock:processWithAttachments")
        interceptSendToEndpoint("direct:processZip")
            .skipSendToOriginalEndpoint()
            .to("mock:processZip")
        interceptSendToEndpoint("direct:uberInvoice")
            .skipSendToOriginalEndpoint()
            .to("mock:uberInvoice")
      }
    }
    context.getRouteDefinition("filterMessage").adviceWith(context, mockDirect)
    context.start()
  }
  @After
  void stopCamelContext(){ context.stop() }

  @Override
  boolean isUseAdviceWith() { true }

  @Test
  void testSubjectContainsInvoice(){
    String value = headers.get('Subject')
    attachmentsEndpoint.expectedHeaderReceived("Subject", value)
    attachmentsEndpoint.expectedMessageCount(processWithAttachments)
    zipEndpoint.expectedMessageCount(processZip)
    uberEndpoint.expectedMessageCount(uberInvoice)
    template.sendBodyAndHeaders("direct:mail", "Any body", headers)
    attachmentsEndpoint.assertIsSatisfied()
  }

}
