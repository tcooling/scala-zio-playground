package com.tcooling

import zio._
import zio.console._

object Main extends zio.App {

  def run(args: List[String]): URIO[Console, ExitCode] = myAppLogic.exitCode

  val myAppLogic =
    for {
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    } yield ()
}
