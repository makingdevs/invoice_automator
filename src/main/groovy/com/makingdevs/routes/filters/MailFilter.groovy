package com.makingdevs.routes.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.Attachment
import org.apache.camel.AttachmentObjects
import org.apache.camel.Header
import org.apache.camel.Headers

@CompileStatic
class MailFilter {

  static List<String> months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre']

  static boolean hasFilesFromAnInvoice(@AttachmentObjects Map<String, Attachment> attachments){
    attachments.any { String fileName, data ->
      fileName.contains(".xml")
    } && attachments.any { String fileName, data ->
      fileName.contains(".pdf")
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
    Integer indexMonth = new Date().parse(pattern, headers["Date"] as String)[Calendar.MONTH]
    months.get(indexMonth)
  }

  static Integer extractYearInvoice(@Headers Map headers) {
    Locale.setDefault(Locale.US)
    String pattern = "EEE, d MMM yyyy HH:mm:ss Z"
    new Date().parse(pattern, headers["Date"] as String)[Calendar.YEAR]
  }
}
