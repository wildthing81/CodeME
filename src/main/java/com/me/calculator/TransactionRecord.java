package com.me.calculator;

import com.me.utils.TransactionUtils;

import java.time.LocalDateTime;

public class TransactionRecord {

    private final String transactionId;
    private final String fromAccountId;
    private final String toAccountId;
    private final LocalDateTime createAt;
    private final double amount;
    private final TransactionType transactionType;
    private final String relatedTransaction;

    public TransactionRecord(String transactionId, String fromAccountId, String toAccountId,
                             String createAt, String amount,
                             String transactionType, String relatedTransaction) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.createAt = LocalDateTime.parse(createAt, TransactionUtils.dateTimeFormatter);
        this.amount = Double.parseDouble(amount);
        this.transactionType = TransactionType.valueOf(transactionType);
        this.relatedTransaction = relatedTransaction;
    }

    public String getTransactionId() {
        return transactionId;
    }


    public String getFromAccountId() {
        return fromAccountId;
    }


    public String getToAccountId() {
        return toAccountId;
    }


    public LocalDateTime getCreateAt() {
        return createAt;
    }


    public double getAmount() {
        return amount;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getRelatedTransaction() {
        return relatedTransaction;
    }

}
