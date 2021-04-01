//package com.phone
//
//import zio.console.Console
//import zio.{Task, URIO, ZIO}
//
//class ScalaMeetingZio {
//
//  // If want to execute program, need to pass program to a runtime
//  val hello: Task[Unit] = ZIO(println("Hello"))
//
//  val rts: zio.Runtime[zio.ZEnv] = zio.Runtime.default
//  rts.unsafeRunSync(hello)
//  val aa: ZIO[Console, Throwable, Int] = ???
//
//  // Can refactor code and keep same semantics
//  // Zio vs Future
//
//  // Zio can fail with anything, a String or Int etc.
//
//  // Any, can fail with Int or succeed with String
//  val res20: ZIO[Any, Int, String] = if (2 < 3) ZIO.fail(8) else ZIO.succeed("String")
//
//  // When run this, it cannot fail
//  val res21: URIO[Any, String] = res20.orElseSucceed("World")
//
//  // There is no implicit in Zio??? In first param accumulate requirement needed to run program
//
//  val boolProgram: URIO[Boolean, Boolean] = ZIO.environment[Boolean]
//  // rts.unsafeRunSync(boolProgram) - will not compile
//
//  // Dependencies decoratively build up, accumulate in type
//
//  // Combinators
//
//}
