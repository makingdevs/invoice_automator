package com.makingdevs.exception

class RemovedLinkException extends Exception {

  RemovedLinkException(String msg) {
    super(msg)
  }

  RemovedLinkException(String msg, Throwable cause) {
    super(msg, cause)
  }

}