package com.me.calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TransactionType {

    PAYMENT,
    REVERSAL;

    public static List<String> enumValuesAsList() {
       return Stream.of(TransactionType.values()).map(TransactionType::name).collect(Collectors.toList());
    }
}
