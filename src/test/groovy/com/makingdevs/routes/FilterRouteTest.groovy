package com.makingdevs.routes

import org.apache.camel.EndpointInject
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

/**
 * Created by makingdevs on 3/27/17.
 */
class InvoiceRouteTest extends CamelTestSupport {

  @EndpointInject(uri = "mock:start")
  protected MockEndpoint resultEndpoint

  @Produce(uri = "direct:mail")
  protected ProducerTemplate template

  @Test
  void testSubjectContainsInvoice(){
    Map<String,String> headers = ["Subject":"FACTURA ABRIL"]
    resultEndpoint.expectedHeaderReceived("Subject", "FACTURA ABRIL")
    resultEndpoint.expectedMessageCount(4)
    template.sendBodyAndHeaders("Any body", headers)

    resultEndpoint.assertIsSatisfied()

  }

  void testSendMatchingMessage() throws Exception {
    String expectedBody = "<matched/>";

    resultEndpoint.expectedBodiesReceived(expectedBody);

    template.sendBodyAndHeader(expectedBody, "foo", "bar");

    resultEndpoint.assertIsSatisfied();
  }

  void testSendNotMatchingMessage() throws Exception {
    resultEndpoint.expectedMessageCount(0);

    template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");

    resultEndpoint.assertIsSatisfied();
  }

  @Override
  protected RouteBuilder createRouteBuilder() {
    new FilterRoute()
  }
}
