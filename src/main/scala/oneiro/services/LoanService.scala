package oneiro.services

import cats.effect.IO
import oneiro.clients.LoanRepository
import oneiro.domain.Loan
import oneiro.services.LoanServiceError.{EmptyName, ExistingLoan}
import squants.market.GBP

import java.time.LocalDate

sealed trait LoanServiceError

object LoanServiceError {
  case object EmptyName extends LoanServiceError
  case object ExistingLoan extends LoanServiceError
}

class LoanService(loanRepository: LoanRepository) {
  def createLoan(loan: Loan): IO[Either[LoanServiceError, Unit]] = {
    if (loan.name == "") IO.pure(Left(EmptyName))
    else {
      loanRepository.get(loan.name).flatMap {
        case None =>
          loanRepository.upsert(loan).map(Right(_))
        case Some(_) =>
          IO.pure(Left(ExistingLoan))
      }
    }
  }

  def getLoan(name: String): IO[Option[Loan]] =
    IO.pure(
      Some(Loan("some-loan", LocalDate.of(2014, 1, 21), LocalDate.of(2020, 1, 21), GBP(10000), 0.05, 0.02))
    )

  def listLoans: IO[List[Loan]] =
    IO.pure(
      List(Loan("some-loan", LocalDate.of(2014, 1, 21), LocalDate.of(2020, 1, 21), GBP(10000), 0.05, 0.02)
      ))

  def updateLoan(name: String): IO[Unit] = ???
}
