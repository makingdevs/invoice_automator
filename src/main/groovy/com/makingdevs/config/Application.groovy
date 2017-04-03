package com.makingdevs.config

import groovy.transform.CompileStatic

/**
 * Created by makingdevs on 3/27/17.
 */
@Singleton
@CompileStatic
class Application {

  static ConfigObject configuration = new ConfigObject()

  ConfigObject getConfiguration(){
    String environment = System.getenv("ENVIRONMENT") ?: "development"
    if(configuration.isEmpty()){
      String path = "${System.properties['user.home']}/.automator/configuration.groovy"
      File file = new File(path)
      if(!file.exists())
        throw new RuntimeException("""
          The configuration file doesn't exist,
          please check the Wiki to copy and setup your environent""")
      configuration = new ConfigSlurper().parse(file.toURI().toURL())
    }
    configuration.get(environment) as ConfigObject
  }



}
