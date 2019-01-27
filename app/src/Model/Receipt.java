package Model;

import java.util.Date;
import java.util.List;

public class Receipt {
    private double totalCost;
    private List<Product> productList;
    private String companyName;
    private Date transactionDate;

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

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

}