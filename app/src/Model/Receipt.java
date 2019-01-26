package Model;

import java.awt.Image;
import java.util.Date;
import java.util.List;

public class Receipt {
    private int serialID;
    private double totalCost;
    private List<Product> productList;
    private String companyName;
    private Date transactionDate;
    private Image recieptImage;

    public Receipt(int serialID, double totalCost, List<Product> productList, String companyName, Date transactionDate, Image recieptImage) {
        this.serialID = serialID;
        this.totalCost = totalCost;
        this.productList = productList;
        this.companyName = companyName;
        this.transactionDate = transactionDate;
        this.recieptImage = recieptImage;
    }

    public Receipt() {
        this.serialID = 0;
        this.totalCost = 0.0;
        this.productList = null;
        this.companyName = null;
        this.transactionDate = null;
        this.recieptImage = null;
    }

    public int getSerialID() {
        return serialID;
    }

    public void setSerialID(int serialID) {
        this.serialID = serialID;
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

    public Image getRecieptImage() {
        return recieptImage;
    }

    public void setRecieptImage(Image recieptImage) {
        this.recieptImage = recieptImage;
    }
}