package oneiro.clients

import cats.effect.IO
import cats.effect.kernel.Ref
import oneiro.domain.Loan

import scala.collection.mutable

class LoanRepository(ref: Ref[IO, mutable.Map[String, Loan]]) {
  def upsert(loan: Loan): IO[Unit] =
    ref.update(_.addOne(loan.name -> loan))

  def get(name: String): IO[Option[Loan]] =
    ref.get.map(_.get(name))

  def getAll: IO[List[Loan]] =
    ref.get.map(_.values.toList)
}

