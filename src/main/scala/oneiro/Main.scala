package oneiro

import cats.effect.kernel.{Ref, Resource}
import cats.effect.{ExitCode, IO, IOApp}
import oneiro.clients.LoanRepository
import oneiro.domain.Loan
import oneiro.services.{Command, ExitConsole, LoanConsole, LoanService}

import scala.collection.mutable

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO.println("Welcome to the Loan Interest Calculator.")
      ref <- Ref[IO].of(mutable.Map[String, Loan]())
      loanRepository = new LoanRepository(ref)
      loanService = new LoanService(loanRepository)
      console = new LoanConsole(loanService)
      _ <- inputLoop(console)
    } yield ExitCode.Success

  def inputLoop(console: LoanConsole): IO[Command] =
    for {
      command <- console.commandPrompt
      _ <- console.processCommand(command)
      _ <- if (command != ExitConsole) inputLoop(console) else IO.pure(ExitConsole)
    } yield command
}
