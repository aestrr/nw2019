package com.nwhacks.myapplication.Model;

import android.media.Image;

import org.json.JSONException;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class Receipt {
    private final double totalCost;
    private final List<Product> productList;
    private final String companyName;
    private final Date transactionDate;

    public static Receipt parseImageToReciept(Image image) throws JSONException, ParseException {
        return null; // TODO FIX
    }

    public void printReceipt() {
        System.out.println("");
        System.out.println("Reciept");
        System.out.println("Total cost: " + this.totalCost);
        System.out.println("Company name: " + this.companyName);
        System.out.println("transaction date: " + this.transactionDate.toString());
        System.out.println("Items: ");
        for (Product x : this.productList) {
            System.out.println("Product: " + x.getName() + " Cost: " + x.getCost());
        }
        System.out.println("");
    }

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