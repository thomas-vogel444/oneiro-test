package oneiro

import cats.effect.{ExitCode, IO, IOApp}

import scala.util.Try

sealed trait Command

case object Unknown extends Command

case object ListLoans extends Command

case object ShowExistingLoan extends Command

case object UpdateExistingLoan extends Command

case object CreateLoan extends Command

case object ExitConsole extends Command

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO.println("Welcome to the Loan Interest Calculator.")
      _ <- inputLoop
    } yield ExitCode.Success

  def inputLoop: IO[Command] =
    for {
      command <- commandPrompt
      _ <- processCommand(command)
      _ <- if (command != ExitConsole) inputLoop else IO.pure(ExitConsole)
    } yield command

  def commandPrompt: IO[Command] =
    for {
      _ <- IO.println("Please input the number corresponding to one of the following options:")
      _ <- IO.println("0 - Exit")
      _ <- IO.println("1 - List all loan calculations")
      _ <- IO.println("2 - Show existing loan calculation details")
      _ <- IO.println("3 - Create new loan calculation")
      _ <- IO.println("4 - Modify existing loan")
      line <- IO.readLine
    } yield
      Try(line.toInt).toOption
        .map {
          case 0 => ExitConsole
          case 1 => ListLoans
          case 2 => ShowExistingLoan
          case 3 => UpdateExistingLoan
          case 4 => CreateLoan
          case _ => Unknown
        }.getOrElse(Unknown)

  def processCommand(command: Command): IO[Unit] =
    command match {
      case Unknown => IO.println("Unknown option. Try again.")
      case ListLoans => IO.println("You chose: ListLoans")
      case ShowExistingLoan => IO.println("You chose: ShowExistingLoan")
      case UpdateExistingLoan => IO.println("You chose: UpdateExistingLoan")
      case CreateLoan => IO.println("You chose: CreateLoan")
      case ExitConsole => IO.println("You chose: ExitConsole")
    }
}
