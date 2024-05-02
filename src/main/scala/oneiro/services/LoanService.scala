package oneiro.services

import cats.effect.IO
import oneiro.clients.LoanRepository
import oneiro.domain.Loan
import oneiro.services.LoanServiceError.{EmptyName, ExistingLoan}

sealed trait LoanServiceError

object LoanServiceError {
  case object EmptyName extends LoanServiceError
  case object ExistingLoan extends LoanServiceError
}

class LoanService(loanTable: LoanRepository) {
  def createLoan(loan: Loan): IO[Either[LoanServiceError, Unit]] = {
    if (loan.name == "") IO.pure(Left(EmptyName))
    else {
      loanTable.get(loan.name).flatMap {
        case None =>
          loanTable.upsert(loan).map(Right(_))
        case Some(_) =>
          IO.pure(Left(ExistingLoan))
      }
    }
  }

  def getLoan(name: String): IO[Loan] = ???

  def listLoans: IO[List[Loan]] = ???

  def updateLoan(name: String): IO[Unit] = ???
}
