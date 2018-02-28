package com.makingdevs.routes.utils

import org.apache.camel.Exchange
import org.apache.camel.Processor
import wslite.rest.*
import java.util.ArrayList
import java.util.regex.Matcher
import com.makingdevs.exception.RemovedLinkException
import wslite.http.HTTPClientException

class ProcessLinkUber implements Processor {

  @Override
  void process(Exchange exchange) throws Exception {
    String link = exchange.in.body
    byte[] data
    def response
    try{
      def client = new RESTClient(link)
      response = client.get()
      data = response.data
    }catch (HTTPClientException e) {
      throw new RemovedLinkException("Another status code")
    }
    exchange.out.setBody(data, data.class)
    Map headersWS = response.headers
    Map headers = exchange.in.headers
    String regex = /"(.*?)"{1}/
    Matcher matcher = headersWS['Content-Disposition'] =~ regex
    if(matcher.size() > 0){
      headers.put("CamelFileName", (matcher[0][1])) // FILENAME!!!!
      exchange.out.setHeaders(headers)
    }else{
      throw new RemovedLinkException("Not content maybe link were remove")
    }
    
  }

}
