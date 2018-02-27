package com.makingdevs.exception

class NoLinkException extends Exception {

  NoLinkException(String msg) {
    super(msg)
  }

  NoLinkException(String msg, Throwable cause) {
    super(msg, cause)
  }

}