package com.makingdevs.routes.utils

import org.apache.camel.Exchange
import org.apache.camel.Processor
import java.io.InputStream
import java.io.BufferedReader
import java.lang.StringBuilder
import com.makingdevs.exception.XmlUnprocessableException
/**
 * Created by neodevelop on 14/04/17.
 */
class ProcessInvoiceDetail implements Processor {
  @Override
  void process(Exchange exchange) throws Exception {

    Map headers = exchange.in.headers
    String fileName = headers['CamelFileName']
    if(fileName.endsWith(".xml")){
      
      Map detail = new  HashMap()
      
      InputStream inputStream = null 
      if(exchange.in.body instanceof InputStream){
        inputStream = exchange.in.body
      }else{
        byte[] data = exchange.in.body
        inputStream = new ByteArrayInputStream(data)
        
      }

      BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
      StringBuilder sb = new StringBuilder()
      String inline = ""
      while ((inline = inputReader.readLine()) != null) {
        sb.append(inline)
      }
      String xml = sb.toString().trim().replaceFirst("^([\\W]+)<","<").toLowerCase()
      if (xml.contains("<!doctype html>")) {
        throw new XmlUnprocessableException("The content of XMl is unprocessable")
      }
      def xmlParsed = new XmlParser().parseText(xml)
      def cfdiNs = new groovy.xml.Namespace("http://www.sat.gob.mx/cfd/3", "cfdi")
      def emitter = xmlParsed[cfdiNs.emisor].@nombre
      detail.put("emitter", emitter.first())
        
      detail.put("expeditionDate", xmlParsed.@fecha)
      String concepts = ""
      xmlParsed[cfdiNs.conceptos][cfdiNs.concepto].each { concepts += it.@descripcion +", "}
      detail.put("concepts", concepts)

      detail.put("amount", xmlParsed.@total)
      def taxes = xmlParsed[cfdiNs.impuestos][cfdiNs.traslados][cfdiNs.traslado].@importe
      detail.put("taxes", taxes.first())
      headers.putAll(detail)
      exchange.out.setHeaders(headers)
      exchange.out.setBody(exchange.in.body)
    }
  }
}
