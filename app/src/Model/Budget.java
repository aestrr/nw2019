package Model;

import java.util.Calendar;
import java.util.Date;

public class Budget {
    private final double initialMonthlyBudget;
    private double currentBudget;
    private final Calendar initialBudgetSetTime;

    public Budget(double monthlyBudget) {
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
        if (Calendar.getInstance() == targetDate);
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

}
