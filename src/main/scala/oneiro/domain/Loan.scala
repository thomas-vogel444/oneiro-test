package oneiro.domain

import oneiro.domain.SimpleInterest.interestAccrued
import squants.Money
import squants.market.GBP

import java.time.LocalDate
import java.time.temporal.ChronoUnit

case class Loan(name: String, startDate: LocalDate, endDate: LocalDate, amount: Money, baseInterestRate: Double, marginInterestRate: Double) {

  def dailyInterestAccrued(accrualDate: LocalDate): Money =
    interestAccruedOnLoan(accrualDate, baseInterestRate)

  def dailyInterestAmountWithoutMargin: Money =
    amount * 1/365 * baseInterestRate

  def totalInterestAccrued(accrualDate: LocalDate): Money =
    interestAccruedOnLoan(accrualDate, marginInterestRate + baseInterestRate)

  private def interestAccruedOnLoan(accrualDate: LocalDate, interestRate: Double): Money = {
    if (accrualDate.isAfter(endDate))
      interestAccrued(startDate, endDate, interestRate, amount)
    else
      interestAccrued(startDate, accrualDate, interestRate, amount)
  }
}

object SimpleInterest {
  def interestAccrued(start: LocalDate, end: LocalDate, interest: Double, amount: Money): Money =
    if (end.isBefore(start)) GBP(0)
    else amount * ChronoUnit.DAYS.between(start, end)/365 * interest
}

