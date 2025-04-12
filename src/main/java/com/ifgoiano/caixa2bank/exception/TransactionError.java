package com.ifgoiano.caixa2bank.exception;

public class TransactionError extends RuntimeException {

    public TransactionError(String msg) {
        super(msg);
    }


}
