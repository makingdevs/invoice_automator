package com.makingdevs.routes.utils

import groovy.transform.CompileStatic
import org.apache.camel.Attachment
import org.apache.camel.AttachmentObjects
import org.apache.camel.Headers

@CompileStatic
class UtilsForRoutes {

  static List<String> months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre']

  static boolean hasFilesFromAnInvoice(@AttachmentObjects Map<String, Attachment> attachments){
    attachments.any { String fileName, data ->
      fileName.toLowerCase().contains(".xml")
    } && attachments.any { String fileName, data ->
      fileName.toLowerCase().contains(".pdf")
    }
  }

  static boolean hasZipFile(@AttachmentObjects Map<String, Attachment> attachments){
    attachments.any { String fileName, data ->
      fileName.contains(".zip")
    }
  }
  static String extractMonthInvoice(@Headers Map headers) {
    Locale.setDefault(Locale.US)
    String pattern = "EEE, d MMM yyyy HH:mm:ss Z"
    String textDate = headers["Date"]
    if(textDate ==~ /\d{2} .{3} \d{4} \d{2}:\d{2}:\d{2} -\d{4}/)
      pattern = "d MMM yyyy HH:mm:ss Z"
    Integer indexMonth = new Date().parse(pattern, textDate as String)[Calendar.MONTH]
    "${indexMonth + 1}_${months.get(indexMonth)}"
  }

  static String extractExpeditionDateMonth(@Headers Map headers) {
    println "extractExpeditionDateMonth"
    Locale.setDefault(Locale.US)
    println "1"
    String pattern = "EEE, d MMM yyyy HH:mm:ss Z"
    String textDate = headers["expeditionDate"]
    println textDate
    if(textDate){
      if(textDate ==~ /\d{4}-\d{2}-\d{2}t\d{2}:\d{2}:\d{2}/)
        pattern = "yyyy-MM-dd't'HH:mm:ss"
      Integer indexMonth = new Date().parse(pattern, textDate as String)[Calendar.MONTH]
      "${indexMonth + 1}_${months.get(indexMonth)}"
    }else{
      null
    }
  }
  
  static Integer extractYearInvoice(@Headers Map headers) {
    Locale.setDefault(Locale.US)
    String pattern = "EEE, d MMM yyyy HH:mm:ss Z"
    String textDate = headers["Date"]
    if(textDate ==~ /\d{2} .{3} \d{4} \d{2}:\d{2}:\d{2} -\d{4}/)
      pattern = "d MMM yyyy HH:mm:ss Z"
    new Date().parse(pattern, textDate as String)[Calendar.YEAR]
  }

}
