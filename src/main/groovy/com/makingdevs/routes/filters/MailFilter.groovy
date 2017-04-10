package com.makingdevs.routes.filters

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.camel.Attachment
import org.apache.camel.AttachmentObjects
import org.apache.camel.Header

@CompileStatic
class MailFilter {
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
}
