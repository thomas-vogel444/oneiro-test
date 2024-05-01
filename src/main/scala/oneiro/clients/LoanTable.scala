package oneiro.clients

import cats.effect.IO
import oneiro.services.Loan

class LoanTable {
  def insert(): IO[Unit] = ???

  def update(): IO[Unit] = ???

  def get(name: String): IO[Option[Loan]] = ???
}
