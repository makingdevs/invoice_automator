package com.makingdevs.exception

class XmlUnprocessableException extends Exception {

  XmlUnprocessableException(String msg) {
    super(msg)
  }

  XmlUnprocessableException(String msg, Throwable cause) {
    super(msg, cause)
  }

}