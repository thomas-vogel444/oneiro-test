package oneiro.clients

import cats.effect.IO
import oneiro.domain.Loan

class LoanTable {
  def insert(loan: Loan): IO[Unit] = ???

  def update(): IO[Unit] = ???

  def get(name: String): IO[Option[Loan]] = ???
}
