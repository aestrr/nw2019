package com.nwhacks.myapplication.Model;

import java.util.Calendar;
import java.util.Date;

public class Budget {
    private Budget budget;
    private double initialMonthlyBudget;
    private double currentBudget;
    private Calendar initialBudgetSetTime;

    public Budget getInstance() {
        if (budget == null) {
            budget = new Budget(0.0);
        }

        return budget;
    }

    private Budget(double monthlyBudget) {
        this.initialMonthlyBudget = monthlyBudget;
        this.currentBudget = monthlyBudget;
        this.initialBudgetSetTime = Calendar.getInstance();
    }

    public void spend(double amount) {
        currentBudget -= amount;
    }

    public void checkIfOneMonthPassed() {
        Calendar targetDate = initialBudgetSetTime;
        targetDate.add(Calendar.DAY_OF_MONTH, 28);
        if (Calendar.getInstance() == targetDate) {
            currentBudget = initialMonthlyBudget;
        }
    }

    public double getInitialMonthlyBudget() {
        return initialMonthlyBudget;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public Date getInitialBudgetSetTime() {
        return initialBudgetSetTime.getTime();
    }

    public double getBudgetUsed() {
        return initialMonthlyBudget - currentBudget;
    }

    public void setInitialMonthlyBudget(double initialMonthlyBudget) {
        this.initialMonthlyBudget = initialMonthlyBudget;
    }

    public void setInitialBudgetSetTime(Calendar initialBudgetSetTime) {
        this.initialBudgetSetTime = initialBudgetSetTime;
    }

    public void setCurrentBudget(double initialMonthlyBudget) {
        this.currentBudget = initialMonthlyBudget;
    }
}
