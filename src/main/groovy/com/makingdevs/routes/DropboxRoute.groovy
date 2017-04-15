package com.makingdevs.routes

import com.makingdevs.config.Application
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository

/**
 * Created by neodevelop on 14/04/17.
 */
class DropboxRoute extends RouteBuilder {

  ConfigObject configuration = Application.instance.configuration

  String localPath = configuration.get("file")["localPath"]
  String dropboxClientId = configuration.get("dropbox")["clientIdentifier"]
  String dropboxAccessToken = configuration.get("dropbox")["accessToken"]

  @Override
  void configure() throws Exception {
    from("file:${localPath}?delay=5000&recursive=true")
    .to("""\
          dropbox://put?\
          accessToken=${dropboxAccessToken}&\
          clientIdentifier=${dropboxClientId}&\
          localPath=${localPath}/\${header.fileName}&\
          remotePath=/facturacion&\
          uploadMode=force""".replace(" ","").trim())

  }

}
