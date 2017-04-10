package com.makingdevs.routes

import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.ExchangePattern
import org.apache.camel.Processor
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultAttachment
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.apache.camel.Message

import javax.activation.FileDataSource

/**
 * Created by makingdevs on 4/10/17.
 */
class AttachmentsRouteTest extends CamelTestSupport {

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

  @Test
  void testMailContainsAttachments(){
    resultEndpoint.expectedMessageCount(1)
    template.send("direct:mail", new Processor() {
      @Override
      void process(Exchange exchange) throws Exception {
        exchange.setPattern(ExchangePattern.InOut)
        Message m = exchange.getIn()
        m.addAttachment("1141-1014985.pdf", new DefaultAttachment(new FileDataSource("src/test/resources/1141-1014985.pdf")))
        m.addAttachment("1141-1014985.xml", new DefaultAttachment(new FileDataSource("src/test/resources/1141-1014985.xml")))
      }
    })
    resultEndpoint.assertIsSatisfied()
  }
}
