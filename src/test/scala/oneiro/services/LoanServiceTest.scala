package oneiro.services

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.effect.testing.scalatest.AsyncIOSpec
import oneiro.clients.LoanRepository
import oneiro.domain.Loan
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.collection.mutable

class LoanServiceTest extends AsyncWordSpec with AsyncIOSpec with Matchers with EitherValues {

  def loanServiceIO(initialMap: mutable.Map[String, Loan]): IO[LoanService] =
    for {
      ref <- Ref[IO].of(initialMap)
      loanRepository = new LoanRepository(ref)
      loanService = new LoanService(loanRepository)
    } yield loanService

  "LoanService.createLoan" should {
    "return an error given a loan with an empty name" in {
      val loan = Loan("")
      val initialMap = mutable.Map[String, Loan]()

      for {
        loanService <- loanServiceIO(initialMap)
        result = loanService.createLoan(loan).unsafeRunSync()
      } yield result.left.value shouldBe a[LoanServiceError]
    }

    "return an error given a loan with a name conflicting with another loan" in {
      val existingLoan = Loan("existing-loan")

      val initialMap = mutable.Map[String, Loan](existingLoan.name -> existingLoan)

      for {
        loanService <- loanServiceIO(initialMap)
        result = loanService.createLoan(existingLoan).unsafeRunSync()
      } yield result.left.value shouldBe a[LoanServiceError]
    }

    "succeed given a non-existing loan" in {
      val loan = Loan("some-name")
      val initialMap = mutable.Map[String, Loan]()

      for {
        loanService <- loanServiceIO(initialMap)
        result = loanService.createLoan(loan).unsafeRunSync()
      } yield result.value shouldBe()
    }

  }
}
