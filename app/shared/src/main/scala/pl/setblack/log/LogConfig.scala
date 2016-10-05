package pl.setblack.log

import slogging.{LogLevel, LoggerConfig, PrintLoggerFactory}

object LogConfig {

  def initConfig() = {
    LoggerConfig.factory = PrintLoggerFactory()


    LoggerConfig.level = LogLevel.DEBUG
  }
}
