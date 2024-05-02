package oneiro.services

import cats.effect.IO

import scala.util.Try

sealed trait Command

case object Unknown extends Command

case object ListLoans extends Command

case object ShowExistingLoan extends Command

case object UpdateExistingLoan extends Command

case object CreateLoan extends Command

case object ExitConsole extends Command

class LoanConsole(loanService: LoanService) {

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
      case ListLoans =>
        IO.println("Loans currently in the database:") *>
          loanService.listLoans.flatMap { loans =>
            loans.map { loan =>
              IO.println(s"${loan.name}: ${loan.startDate} -> ${loan.endDate}, amount: ${loan.amount}, base: ${loan.baseInterestRate}, margin: ${loan.marginInterestRate}")
            }.reduce(_ *> _)
          }
      case ShowExistingLoan =>
        for {
          _ <- IO.println("Input the name of the loan you would like to access...")
          loanName <- IO.readLine
          loanOpt <- loanService.getLoan(loanName)
          _ <-
            loanOpt.map { loan =>
              IO.println(s"${loan.name}: ${loan.startDate} -> ${loan.endDate}, amount: ${loan.amount}, base: ${loan.baseInterestRate}, margin: ${loan.marginInterestRate}")
            }.getOrElse(IO.println("The name of the loan isn't in the database."))
        } yield ()
      case UpdateExistingLoan => IO.println("You chose: UpdateExistingLoan")
      case CreateLoan =>
        for {
          _ <- IO.println("Please input the following information:")
          _ <- IO.println("Name of the loan:")
          name <- IO.readLine
          _ <- IO.println("Start date (format yyyy-mm-dd)")
          startDate <- IO.readLine
          _ <- IO.println("End date (format yyyy-mm-dd)")
          endDate <- IO.readLine
          _ <- IO.println("Amount")
          amount <- IO.readLine
          _ <- IO.println("Currency (e.g. GBP, USD, etc...)")
          currency <- IO.readLine
          _ <- IO.println("Base rate (write 0.05 for 5%)")
          baseRate <- IO.readLine
          _ <- IO.println("Margin rate (write 0.05 for 5%)")
          marginRate <- IO.readLine
        } yield ()
      case ExitConsole => IO.println("Exiting... Have a nice day!")
    }
}
