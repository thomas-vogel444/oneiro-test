package oneiro.services

import cats.effect.IO
import oneiro.clients.LoanTable

case class Loan(name: String)

sealed trait LoanServiceError
case object EmptyName extends LoanServiceError
case object ExistingLoan extends LoanServiceError

class LoanService(loanTable: LoanTable) {
  def createLoan(loan: Loan): IO[Either[LoanServiceError, String]] = {
    if (loan.name == "") IO.pure(Left(EmptyName))
    else {
      loanTable.get(loan.name).map {
        case None => Left(ExistingLoan)
      }
    }
  }

  def getLoan(name: String): IO[Loan] = ???

  def listLoans: IO[List[Loan]] = ???

  def updateLoan(name: String): IO[Unit] = ???
}
