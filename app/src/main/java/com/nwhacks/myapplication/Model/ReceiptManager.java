package com.nwhacks.myapplication.Model;

import java.util.ArrayList;
import java.util.List;

public class ReceiptManager {

    private List<Receipt> receipts;
    private static ReceiptManager receiptManager;

    public static ReceiptManager getInstance() {
        if (receiptManager == null) {
            receiptManager = new ReceiptManager();
        }

        return receiptManager;
    }

    private ReceiptManager() {
        receipts = new ArrayList<>();
    }

    public void addReceipt(Receipt receipt) {
        receipts.add(receipt);
    }

    public Receipt getReceipt() {
         return receipts.get(receipts.size() - 1);
    }
}
