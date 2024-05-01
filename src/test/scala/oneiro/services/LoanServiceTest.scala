package oneiro.services

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import oneiro.clients.LoanTable
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory

class LoanServiceTest extends AnyWordSpec with Matchers with EitherValues with MockFactory {

  val loanTable = mock[LoanTable]
  val loanService = new LoanService(loanTable)

  "LoanService.createLoan" should {
    "return an error given a loan with an empty name" in {
      val loan = Loan("")

      val result = loanService.createLoan(loan).unsafeRunSync()

      result.left.value shouldBe a[LoanServiceError]
    }

    "return an error given a loan with a name conflicting with another loan" in {
      val loan = Loan("some-name")
      (loanTable.get _).expects(loan.name).returning(IO.pure(None))

      val result = loanService.createLoan(loan).unsafeRunSync()

      result.left.value shouldBe a[LoanServiceError]
    }

  }
}
