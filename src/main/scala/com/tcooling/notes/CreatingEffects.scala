package com.tcooling.notes

import java.io.IOException
import java.net.{ServerSocket, Socket}

import zio.blocking.{Blocking, blocking, effectBlocking, effectBlockingCancelable}
import zio.{IO, RIO, Task, UIO, URIO, ZIO}

import scala.concurrent.Future
import scala.io.StdIn
import scala.io.{Codec, Source}
import scala.util.Try

/**
 * 2 - Creating Effects
 *
 * @see https://zio.dev/docs/overview/overview_creating_effects
 */
object CreatingEffects {

  /**
   * From Success Values
   *
   * @see https://zio.dev/docs/overview/overview_creating_effects#from-success-values
   */
  object FromSuccessValues {

    // Using the ZIO.succeed method you can create an effect that succeeds with specific value
    val s1: UIO[Int] = ZIO.succeed(42)

    // You can also use methods in the companion objects of the ZIO type aliases:
    val s2: Task[Int] = Task.succeed(42)

    // The succeed method is intended for values which do not have any side effects. If you know that your
    // value does have side effects, consider using ZIO.effectTotal for clarity
    val now: UIO[Long] = ZIO.effectTotal(System.currentTimeMillis())
    // The value inside a successful effect constructed with ZIO.effectTotal will only be constructed if absolutely required.

  }

  /**
   * From Failure Values
   *
   * @see https://zio.dev/docs/overview/overview_creating_effects#from-failure-values
   */
  object FromFailureValues {

    // Using the ZIO.fail method, you can create an effect that models failure:
    val f1: IO[String, Nothing] = ZIO.fail("Uh oh!")
    // No restriction on ZIO error type, can use String/Exception or custom data type

    // Many applications will model failures with classes that extend Throwable or Exception:
    val f2: Task[Nothing] = Task.fail(new Exception("Uh oh!"))
    // Note: UIO companion object does not have UIO.fail as it cannot fail

  }

  /**
   * From Scala Values
   *
   * @see https://zio.dev/docs/overview/overview_creating_effects#from-scala-values
   */
  object FromScalaValues {

    // Scala's standard library contains a number of data types that can be converted to ZIO effects

    /**
     * Option
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#option
     */

    // An Option can be converted into a ZIO effect using ZIO.fromOption:
    val zOption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))

    // Error type of Option[Nothing] provides no details of the error, so can change the Option[Nothing] into a more
    // specific error type using: ZIO.mapError
    val zOption2: IO[String, Int] = zOption.mapError(_ => "It wasn't there!")

    // You can also readily compose it with other operators while preserving the optional nature of the
    // result (similar to an OptionT)
    final case class User(userId: String, teamId: String)
    final case class Team(teamId: String)
    val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))
    def getUser(userId: String): IO[Throwable, Option[User]] = ZIO.succeed(Some(User(userId, "teamId")))
    def getTeam(teamId: String): IO[Throwable, Team] = ZIO.succeed(Team(teamId))

    val result: IO[Throwable, Option[(User, Team)]] = (for {
      id   <- maybeId
      user <- getUser(id).some
      team <- getTeam(user.teamId).asSomeError
    } yield (user, team)).optional

    /**
     * Either
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#either
     */

    // An Either can be converted into a ZIO effect using ZIO.fromEither:
    val zEither: IO[Nothing, String] = ZIO.fromEither(Right("Success!"))

    // The error type of the resulting effect will be whatever type the Left case has, while the success
    // type will be whatever type the Right case has.

    /**
     * Try
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#try
     */

    // A Try value can be converted into a ZIO effect using ZIO.fromTry:
    val zTry: Task[Int] = ZIO.fromTry(Try(42 / 0))

    // The error type of the resulting effect will always be Throwable, because Try can only fail
    // with values of type Throwable.

    /**
     * Function
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#function
     */

    // A function A => B can be converted into a ZIO effect with ZIO.fromFunction:
    val zFun: URIO[Int, Int] = ZIO.fromFunction((i: Int) => i * i)

    // The environment type of the effect is A (the input type of the function), because in order to run the effect,
    // it must be supplied with a value of this type.

    /**
     * Future
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#future
     */

    // A Future can be converted into a ZIO effect using ZIO.fromFuture:
    lazy val future: Future[String] = Future.successful("Hello!")

    val zFuture: Task[String] = ZIO.fromFuture { implicit ec =>
      future.map(_ => "Goodbye!")
    }

    // The function passed to fromFuture is passed an ExecutionContext, which allows ZIO to manage where the Future
    // runs (of course, you can ignore this ExecutionContext).
    // The error type of the resulting effect will always be Throwable, because Future can only fail with values of type Throwable.

  }

  /**
   * From Side-Effects
   *
   * @see https://zio.dev/docs/overview/overview_creating_effects#from-side-effects
   */
  object FromSideEffects {

    // ZIO can convert both synchronous and asynchronous side-effects into ZIO effects (pure values).
    // These functions can be used to wrap procedural code, allowing you to seamlessly use all features of ZIO
    // with legacy Scala and Java code, as well as third-party libraries.

    /**
     * Synchronous Side-Effects
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#synchronous-side-effects
     */

    // A synchronous side-effect can be converted into a ZIO effect using ZIO.effect:
    val getStrLn: Task[String] = ZIO.effect(StdIn.readLine())
    // The error type of the resulting effect will always be Throwable, because side-effects may throw
    // exceptions with any value of type Throwable.

    // If a given side-effect is known to not throw any exceptions, then the side-effect can be converted
    // into a ZIO effect using ZIO.effectTotal:
    def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))
    // Be careful using ZIO.effectTotal, when in doubt about if a side effect is total prefer ZIO.effect to convert
    // the effect

    // TODO: below does not compile from the official docs??
    // If you wish to refine the error type of an effect (by treating other errors as fatal), then you
    // can use the ZIO.refineToOrDie method:
    //val getStrLn2: IO[IOException, String] = ZIO.effect(StdIn.readLine()).refineToOrDie[IOException]
    val getStrLn2: Task[String] = ZIO.effect(StdIn.readLine())
    val res: IO[IOException, String] = getStrLn2.refineToOrDie[IOException]

    /**
     * Asynchronous Side-Effects
     *
     * @see https://zio.dev/docs/overview/overview_creating_effects#asynchronous-side-effects
     */

    // An asynchronous side-effect with a callback-based API can be converted into a ZIO effect using ZIO.effectAsync:
    final case class User()
    final case class AuthError()
    object legacy {
      def login(onSuccess: User => Unit, onFailure: AuthError => Unit): Unit = ???
    }

    val login: IO[AuthError, User] =
      IO.effectAsync[AuthError, User] { callback =>
        legacy.login(
          user => callback(IO.succeed(user)),
          err  => callback(IO.fail(err))
        )
      }

    // Asynchronous ZIO effects are much easier to use than callback-based APIs, and they benefit from ZIO features
    // like interruption, resource-safety, and superior error handling.

  }

  /**
   * Blocking Synchronous Side-Effects
   *
   * @see https://zio.dev/docs/overview/overview_creating_effects#blocking-synchronous-side-effects
   */
  object BlockingSynchronousSideEffects {

    // Some side effects use blocking IO or otherwise put a thread into a waiting state. If not carefully managed,
    // these side-effects can deplete threads from your application's main thread pool, resulting in work starvation.

    // ZIO provides the zio.blocking package, which can be used to safely convert such blocking side-effects
    // into ZIO effects.

    // A blocking side-effect can be converted directly into a ZIO effect blocking with the effectBlocking method:
    val sleeping: RIO[Blocking, Unit] = effectBlocking(Thread.sleep(Long.MaxValue))
    // The resulting effect will be executed on a separate thread pool designed specifically for blocking effects.

    // Blocking side-effects can be interrupted by invoking Thread.interrupt using the effectBlockingInterrupt method.

    // Some blocking side-effects can only be interrupted by invoking a cancellation effect. You can convert
    // these side-effects using the effectBlockingCancelable method:
    def accept(l: ServerSocket): RIO[Blocking, Socket] =
      effectBlockingCancelable(l.accept())(UIO.effectTotal(l.close()))

    // If a side-effect has already been converted into a ZIO effect, then instead of effectBlocking,
    // the blocking method can be used to ensure the effect will be executed on the blocking thread pool:
    def download(url: String): Task[String] = Task.effect {
      val urlSource = Source.fromURL(url)(Codec.UTF8)
      val sourceString = urlSource.mkString
      urlSource.close()
      sourceString
    }

    def safeDownload(url: String): ZIO[Blocking, Throwable, String] = blocking(download(url))

  }

}
