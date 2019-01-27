package Model;

import java.util.Date;
import java.util.List;

public class Receipt {
    private final double totalCost;
    private final List<Product> productList;
    private final String companyName;
    private final Date transactionDate;

    public Receipt(String companyName, double totalCost, List<Product> productList, Date transactionDate) {
        this.companyName = companyName;
        this.totalCost = totalCost;
        this.productList = productList;
        this.transactionDate = transactionDate;
    }

    public Receipt() {
        this.companyName = null;
        this.totalCost = 0.0;
        this.productList = null;
        this.transactionDate = null;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

}