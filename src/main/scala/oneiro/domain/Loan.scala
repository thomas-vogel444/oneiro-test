package oneiro.domain

import cats.Show
import oneiro.domain.Loan.interestAccrued
import squants.Money
import squants.market.{Currency, GBP, Money}

import java.time.LocalDate
import java.time.temporal.ChronoUnit

case class Loan(name: String, startDate: LocalDate, endDate: LocalDate, amount: Money, baseInterestRate: Double, marginInterestRate: Double) {

  def dailyInterestAccrued(accrualDate: LocalDate): Money =
    interestAccruedOnLoan(accrualDate, baseInterestRate)

  def dailyInterestAmountWithoutMargin: Money =
    amount * 1 / 365 * baseInterestRate

  def totalInterestAccrued(accrualDate: LocalDate): Money =
    interestAccruedOnLoan(accrualDate, marginInterestRate + baseInterestRate)

  private def interestAccruedOnLoan(accrualDate: LocalDate, interestRate: Double): Money = {
    if (accrualDate.isAfter(endDate))
      interestAccrued(startDate, endDate, interestRate, amount)
    else
      interestAccrued(startDate, accrualDate, interestRate, amount)
  }
}

object Loan {
  /**
   * Should have more granular validation than just being an Option.
   */
  def from(name: String, startDate: LocalDate, endDate: LocalDate, amount: Double, rawCurrency: Currency, baseInterestRate: Double, marginInterestRate: Double): Option[Loan] =
    Option.when(startDate.isBefore(endDate) && amount > 0 && baseInterestRate > 0 && marginInterestRate > 0) {
      Loan(name, startDate, endDate, Money.apply(amount, rawCurrency), baseInterestRate, marginInterestRate)
    }

  def interestAccrued(start: LocalDate, end: LocalDate, interest: Double, amount: Money): Money =
    if (end.isBefore(start)) GBP(0)
    else amount * ChronoUnit.DAYS.between(start, end) / 365 * interest

  val show: Show[Loan] =
    Show.show[Loan](loan => s"${loan.name}: ${loan.startDate} -> ${loan.endDate}, amount: ${loan.amount}, base: ${loan.baseInterestRate}, margin: ${loan.marginInterestRate}")
}

