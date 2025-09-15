package com.ordermanagement.model;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String errorMessage;
    
    public PaymentResult(boolean success, String transactionId, String errorMessage) {
        this.success = success;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
    }
    
    public static PaymentResult success(String transactionId) {
        return new PaymentResult(true, transactionId, null);
    }
    
    public static PaymentResult failure(String errorMessage) {
        return new PaymentResult(false, null, errorMessage);
    }
    
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getErrorMessage() { return errorMessage; }
}