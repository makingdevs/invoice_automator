package com.makingdevs.routes.utils

import org.apache.camel.Exchange
import org.apache.camel.Processor

import javax.activation.DataHandler
import java.io.InputStream
import java.io.BufferedReader
import java.lang.StringBuilder
/**
 * Created by neodevelop on 14/04/17.
 */
class ProcessAttachments implements Processor {
  @Override
  void process(Exchange exchange) throws Exception {
    Map<String, DataHandler> attachments = exchange.in.attachments
    String filename = ""
    DataHandler dh = null
    attachments.each { String f, DataHandler h ->
      filename = f
      dh = h
    }

    byte[] data =  exchange.context.typeConverter.convertTo(
        dh.inputStream.bytes.class, dh.inputStream) as byte[]

    exchange.out.setBody(data, data.class)
    Map headers = exchange.in.headers
    headers.put("CamelFileName", filename) // FILENAME!!!!
    exchange.out.setHeaders(headers)

  }
}
