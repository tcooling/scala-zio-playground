package com.tcooling.notes

import zio.{IO, RIO, Task, UIO, URIO}

/**
 * 1 - Summary
 *
 * @see https://zio.dev/docs/overview/overview_index
 */
object Summary {

  /**
   * ZIO
   *
   * @see https://zio.dev/docs/overview/overview_index#zio
   */

  // ZIO is a library for asynchronous and concurrent programming that is based on
  // pure functional programming.

  // Core of ZIO is a powerful effect type inspired by Haskell's IO monad. This data type lets you solve complex
  // problems with simple, type-safe, testable, and composable code.

  // The ZIO[R, E, A] data type has three type parameters
  // - R - Environment Type, Any means no requirements
  // - E - Failure Type, could be Throwable or Nothing if the effect cannot fail
  // - A - Success Type, the effect may succeed with a value of type A, if this type parameter is Unit it means
  //     - the effect produces no useful information, if it is Nothing then it will run forever (or until failure)

  /**
   * Type Aliases
   *
   * @see https://zio.dev/docs/overview/overview_index#type-aliases
   */

  // ZIO data type is the only effect type in ZIO, however family of type aliases and companion objects that
  // simplify common cases.

  // UIO[A] is type alias for ZIO[Any, Nothing, A] which represents an effect that has no requirements and
  // cannot fail, but can succeed with A (in below example an Int)
  val effect1: UIO[Int] = ???

  // URIO[R, A] is a type alias for ZIO[R, Nothing, A] which represents an effect that requires an R and cannot
  // fail, but can succeed with an A
  val effect2: URIO[Boolean, Int] = ???

  // Task[A] is a type alias for ZIO[Any, Throwable, A] which represents an effect which has no requirements
  // and may fail with a Throwable value or succeed with an A
  val effect3: Task[Int] = ???

  // RIO[R, A] is a type alias for ZIO[R, Throwable, A] which represents an effect that requires an R and may
  // fail with a Throwable value, or succeed with an A
  val effect4: RIO[Boolean, Int] = ???

  // IO[E, A] is a type alias for ZIO[Any, E, A] which represents an effect that has no requirements and may
  // fail with an E or succeed with an A
  val effect5: IO[Exception, Int] = ???

  // If new to functional effects, start with Task type which has single type parameter and is most similar
  // to a Future.

}
