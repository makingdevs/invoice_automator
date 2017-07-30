package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.utils.ProcessAttachments
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.aws.s3.S3Constants
import org.apache.camel.component.mail.SplitAttachmentsExpression
import org.apache.camel.processor.aggregate.AggregationStrategy

@CompileStatic
class InvoiceRoute extends RouteBuilder {

  ConfigObject configuration = Application.instance.configuration

  String s3Endpoint = "aws-s3://${configuration.get('aws')['bucketName']}?accessKey=${configuration.get('aws')['accessKey']}&secretKey=RAW(${configuration.get('aws')['secretKey']})"

  void configure() {

    errorHandler(loggingErrorHandler("com.makingdevs.invoice").level(LoggingLevel.ERROR))

    from("direct:processWithAttachments")
        .split(new SplitAttachmentsExpression())
        .process(new ProcessAttachments())
        .to("direct:storeInLocal")

    from("direct:storeInLocal")
    //.setHeader(S3Constants.CONTENT_LENGTH, simple("file:size"))
    // TODO: Calculate the body size
        .setHeader(S3Constants.KEY, simple('${header.expeditionYear}/${header.expeditionMonth}/${in.header.CamelFileName}'))
        .to(s3Endpoint)
        .process { Exchange e ->
          String message = e.in.headers['CamelAwsS3Key']
          e.out.setBody(message, String)
          e.out.setHeaders(e.in.headers)
        }
        .aggregate(header("Message-ID"), { Exchange oldExchange, Exchange newExchange ->
          if (!oldExchange){
            return newExchange
          }

          String oldBody = oldExchange.in.getBody(String)
          String newBody = newExchange.in.getBody(String)
          oldExchange.in.setBody(oldBody + "\n" + newBody)
          oldExchange
        } as AggregationStrategy).completionTimeout(3000L) // TODO: Arbitrary, deep learn!
        .process { Exchange ex ->
          String message = """\
                                Archivos: ${ex.in.body}
                                \nAsunto: ${ex.in.headers['Subject']}\
                                \nRemitente: ${ex.in.headers['Reply-To'] ?: 'Sin información'}\
                              """.trim()
          ex.in.setBody(message)
        }
        .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")
  }

}
