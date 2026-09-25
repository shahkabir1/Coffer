package com.shahkabir.coffer.util;

public class AccountNumberMasker {

    public static String mask(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            throw new IllegalArgumentException("Invalid account number");
        }

        return "*".repeat(accountNumber.length() - 4)
                + accountNumber.substring(accountNumber.length() - 4);
    }
}
