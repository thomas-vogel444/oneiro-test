package oneiro.services

import cats.data.Validated
import cats.effect.IO
import oneiro.domain.Loan
import oneiro.services.LoanConsole.{getInput, validateCurrency, validateDate, validateDouble}
import squants.market.{Currency, MoneyContext, defaultMoneyContext}

import java.time.LocalDate
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
          case 3 => CreateLoan
          case 4 => UpdateExistingLoan
          case _ => Unknown
        }.getOrElse(Unknown)

  def processCommand(command: Command): IO[Unit] =
    command match {
      case Unknown => IO.println("Unknown option. Try again.")
      case ListLoans =>
        IO.println("Loans currently in the database:") *>
          loanService.listLoans.flatMap {
            _.map(loan => IO.println(Loan.show.show(loan)))
              .reduceOption(_ *> _)
              .getOrElse(IO.println("No loans in the database..."))
          }
      case ShowExistingLoan =>
        for {
          _ <- IO.println("Input the name of the loan you would like to access...")
          loanName <- IO.readLine
          loanOpt <- loanService.getLoan(loanName)
          _ <-
            loanOpt.map(loan => IO.println(Loan.show.show(loan)))
              .getOrElse(IO.println("The name of the loan isn't in the database."))
        } yield ()
      case UpdateExistingLoan => IO.println("You chose: UpdateExistingLoan")
      case CreateLoan =>
        for {
          _ <- IO.println("Please input the following information:")
          _ <- IO.println("Name of the loan:")
          name <- IO.readLine
          startDate <- getInput[LocalDate]("Input start date (format yyyy-mm-dd)")(validateDate)
          endDate <- getInput[LocalDate]("Input end date (format yyyy-mm-dd)")(validateDate)
          amount <- getInput[Double]("Input amount")(validateDouble)
          currency <- getInput[Currency]("Input currency (e.g. GBP, USD, etc...)")(validateCurrency)
          baseRate <- getInput[Double]("Input base rate (write 0.05 for 5%)")(validateDouble)
          marginRate <- getInput[Double]("Input margin rate (write 0.05 for 5%)")(validateDouble)
          loanOpt = Loan.from(name, startDate, endDate, amount, currency, baseRate, marginRate)
          _ <-
            loanOpt.map(loanService.createLoan(_) *> IO.println("Inserted the loan into the database"))
              .getOrElse(IO.println("Invalid loan. You must have made a mistake, e.g. negate amount or interest rate, or start date after end date..."))
        } yield ()
      case ExitConsole => IO.println("Exiting... Have a nice day!")
    }
}

import cats.syntax.all._

object LoanConsole {
  def getInput[A](message: String)(validation: String => Validated[String, A]): IO[A] =
    for {
      _ <- IO.println(message)
      validated <- IO.readLine.map(validation)
      value <- validated.fold(IO.println(_) *> getInput[A](message)(validation), IO.pure)
    } yield value

  implicit val moneyContext: MoneyContext = defaultMoneyContext

  def validateCurrency(raw: String): Validated[String, Currency] =
    Currency(raw).toEither.left.map(_ => "Invalid currency format. Should be in standard form e.g. GBP, USD, etc...").toValidated

  def validateDate(raw: String): Validated[String, LocalDate] =
    Try(LocalDate.parse(raw)).toEither.left.map(_ => "Invalid format for date yyyy-mm-dd").toValidated

  def validateDouble(raw: String): Validated[String, Double] =
    Try(raw.toDouble).toEither.left.map(_ => "Invalid double. Should be a positive decimal e.g. 0.05 for 5%.").toValidated
}
