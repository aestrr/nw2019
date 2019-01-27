package com.nwhacks.myapplication.Model;

import java.util.ArrayList;
import java.util.List;

public class ReceiptManager {

    private List<Receipt> receipts;
    private ReceiptManager receiptManager;

    public ReceiptManager getInstance() {
        if (receiptManager == null) {
            receiptManager = new ReceiptManager();
        }

        return receiptManager;
    }

    private ReceiptManager() {
        receipts = new ArrayList<>();
    }
}
