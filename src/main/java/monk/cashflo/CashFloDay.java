package monk.cashflo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by marksloan on 5/27/14.
 */
public class CashFloDay {
    //This a comment that simulates another developer updating before me
    Date   day;
    Double startAmt = 0.0;
    Double endAmt = 0.0;
    ArrayList<Income> income = new ArrayList<>();
    ArrayList<Expense> expenses = new ArrayList<>();

    public CashFloDay(Date day) {
        this.day = day;
    }

    public ArrayList<Income> getIncome() {
        return income;
    }

    public void setIncome(ArrayList<Income> income) {
        this.income = income;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Double getStartAmt() {
        return startAmt;
    }

    public void setStartAmt(Double startAmt) {
        this.startAmt = startAmt;
    }

    public Double getEndAmt() {
        return endAmt;
    }

    public void setEndAmt(Double endAmt) {
        this.endAmt = endAmt;
    }
}
