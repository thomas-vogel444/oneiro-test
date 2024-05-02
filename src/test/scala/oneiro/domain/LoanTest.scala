package oneiro.domain

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import squants.market.GBP

import java.time.LocalDate

class LoanTest extends AnyWordSpec with Matchers {

  val loan =
    Loan(
      name = "loan",
      startDate = LocalDate.of(2023, 1, 2),
      endDate = LocalDate.of(2024, 1, 2),
      amount = GBP(10000),
      baseInterestRate = 0.05,
      marginInterestRate = 0.02
    )

  /**
   * A simple interest calculator is used for the example tests: https://www.calculatorsoup.com/calculators/financial/simple-interest-plus-principal-calculator.php
   */
  "Loan.dailyInterestAccrued" should {

    "return 0 if the accrual date is before the start date" in {
      val result = loan.dailyInterestAccrued(loan.startDate.minusDays(1))

      result shouldBe GBP(0)
    }

    "return 0 if the accrual date is on the start date" in {
      val result = loan.dailyInterestAccrued(loan.startDate)

      result shouldBe GBP(0)
    }

    "return the right interest given 100 days in the future" in {
      val daysInTheFuture = 100
      val accruedDate = loan.startDate.plusDays(daysInTheFuture)

      val result = loan.dailyInterestAccrued(accruedDate)

      assert(result.amount === GBP(136.99).amount +- 0.01)
    }

    "return the right interest given 200 days in the future" in {
      val daysInTheFuture = 200
      val accruedDate = loan.startDate.plusDays(daysInTheFuture)

      val result = loan.dailyInterestAccrued(accruedDate)

      assert(result.amount === GBP(273.97).amount +- 0.01)
    }

    "return the full interest if the accrued date is past the endDate" in {
      val fullInterest = loan.dailyInterestAccrued(loan.endDate)
      val result = loan.dailyInterestAccrued(loan.endDate.plusDays(20))

      result shouldBe fullInterest
    }
  }

  "Loan.totalInterest" should {
    "return 0 if the accrual date is before the start date" in {
      val result = loan.totalInterestAccrued(loan.startDate.minusDays(1))

      result shouldBe GBP(0)
    }

    "return 0 if the accrual date is on the start date" in {
      val result = loan.totalInterestAccrued(loan.startDate)

      result shouldBe GBP(0)
    }

    "return the right interest given 100 days in the future" in {
      val daysInTheFuture = 100
      val accruedDate = loan.startDate.plusDays(daysInTheFuture)

      val result = loan.totalInterestAccrued(accruedDate)

      assert(result.amount === GBP(191.78).amount +- 0.01)
    }

    "return the right interest given 200 days in the future" in {
      val daysInTheFuture = 200
      val accruedDate = loan.startDate.plusDays(daysInTheFuture)

      val result = loan.totalInterestAccrued(accruedDate)

      assert(result.amount === GBP(383.56).amount +- 0.01)
    }

    "return the full interest if the accrued date is past the endDate" in {
      val fullInterest = loan.totalInterestAccrued(loan.endDate)
      val result = loan.totalInterestAccrued(loan.endDate.plusDays(20))

      result shouldBe fullInterest
    }
  }
}
