package com.makingdevs.routes

import com.makingdevs.config.Application
import com.makingdevs.routes.utils.ProcessAttachments
import com.makingdevs.routes.utils.UtilsForRoutes
import groovy.transform.CompileStatic
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.aws.s3.S3Constants
import org.apache.camel.component.mail.SplitAttachmentsExpression

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
          String message = """\
                            Archivo procesado: ${e.in.headers['CamelAwsS3Key']}.\
                            \nAsunto: ${e.in.headers['Subject']}\
                            \nRemitente: ${e.in.headers['Reply-To'] ?: 'Sin información'}\
                          """.trim()
          e.out.setBody(message, String)
          e.out.setHeaders(e.in.headers)
        }
        // TODO: Use an aggregator to send one message
        .to("telegram:bots/${configuration.get('telegram')['token']}?chatId=${configuration.get('telegram')['chatId']}")
  }

}
