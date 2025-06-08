package com.dc.ncsys_springboot.exception;

public class BusinessException extends RuntimeException {
    private final String internalMessage;
    private final String customerMessage;

    // 基础构造器（只传客户消息）
    public BusinessException(String customerMessage) {
        this(customerMessage, customerMessage);
    }

    // 完整构造器（同时传内部和客户消息）
    public BusinessException(String internalMessage, String customerMessage) {
        super(customerMessage);
        this.internalMessage = internalMessage;
        this.customerMessage = customerMessage;
    }

    // Getter方法
    public String getInternalMessage() {
        return internalMessage;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }
}