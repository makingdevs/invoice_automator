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
    if(filename.endsWith(".xml")){
        headers.putAll(pullDetail(data))
    }
    exchange.out.setHeaders(headers)

  }

  private Map pullDetail(byte[] data){
      Map detail = new  HashMap()
      InputStream myInputStream = new ByteArrayInputStream(data)
      BufferedReader inputReader = new BufferedReader(new InputStreamReader(myInputStream))
      StringBuilder sb = new StringBuilder()
      String inline = ""
      while ((inline = inputReader.readLine()) != null) {
        sb.append(inline)
      }
      String xml = sb.toString().toLowerCase()
      def xmlParsed = new XmlParser().parseText(xml)
      def cfdiNs = new groovy.xml.Namespace("http://www.sat.gob.mx/cfd/3", "cfdi")
      
      def emitter = xmlParsed[cfdiNs.emisor].@nombre
      detail.put("emitter", emitter[0])
      
      detail.put("expeditionDate", xmlParsed.@fecha)

      String concepts = ""
      xmlParsed[cfdiNs.conceptos][cfdiNs.concepto].each { concepts += it.@descripcion +", "}
      detail.put("concepts", concepts)

      detail.put("amount", xmlParsed.@total)

      def taxes = xmlParsed[cfdiNs.impuestos][cfdiNs.traslados][cfdiNs.traslado].@importe
      detail.put("taxes", taxes[0])
      detail
  }
}
