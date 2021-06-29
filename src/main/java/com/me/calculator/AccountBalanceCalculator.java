package com.me.calculator;

import com.me.utils.TransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class AccountBalanceCalculator {

    private static final Logger log = LoggerFactory.getLogger(AccountBalanceCalculator.class);

    private List<TransactionRecord> trxRecords = new ArrayList<>();

    private String accountId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public static void main(String[] args) {
        AccountBalanceCalculator calculator = new AccountBalanceCalculator();

        try {
            calculator.loadTransactions();
            calculator.loadInputArgs();
            AbstractMap.SimpleImmutableEntry<Double, Integer> output = calculator.findAccountBalance();
            System.out.print("Relative balance for the period is: ");
            System.out.println(TransactionUtils.currencyFormatter.format(output.getKey()));
            System.out.print("Number of transactions included is:");
            System.out.println(output.getValue());
        } catch (Exception e) {
            log.error("Unable to calculate Account balance");
        }
    }

    public AbstractMap.SimpleImmutableEntry<Double, Integer> findAccountBalance() {
        double accountBalance = 0;
        List<String> balanceTrx = new ArrayList<>();

        for (TransactionRecord trxRecord : trxRecords) {

            if (TransactionUtils.isTimeValidTrx(startTime, endTime, trxRecord)) {
                if (TransactionUtils.isTrxDebit(accountId, trxRecord)) {
                    accountBalance -= trxRecord.getAmount();
                    balanceTrx.add(trxRecord.getTransactionId());
                }

                if (TransactionUtils.isTrxCredit(accountId, trxRecord)) {
                    accountBalance += trxRecord.getAmount();
                    balanceTrx.add(trxRecord.getTransactionId());
                }
            }

            if (TransactionUtils.isTrxDebitReversal(accountId, trxRecord, balanceTrx)) {
                accountBalance += trxRecord.getAmount();
                balanceTrx.remove(trxRecord.getRelatedTransaction());
            }

            if (TransactionUtils.isTrxCreditReversal(accountId, trxRecord, balanceTrx)) {
                accountBalance -= trxRecord.getAmount();
                balanceTrx.remove(trxRecord.getRelatedTransaction());
            }
        }

        return new AbstractMap.SimpleImmutableEntry<>(accountBalance, balanceTrx.size());
    }


    public void loadTransactions() throws IOException, URISyntaxException {

        try (Stream<String> stream = Files.lines(Paths.get(getClass()
                .getClassLoader().getResource("transactions.csv").toURI()), Charset.defaultCharset())
                .skip(1)) {
            stream.forEach(record -> {
                String[] fields = TransactionUtils.validTrxFormat(record);
                if (fields != null)
                    trxRecords.add(new TransactionRecord(
                            fields[0],
                            fields[1],
                            fields[2],
                            fields[3],
                            fields[4],
                            fields[5], fields.length > 6 ? fields[6] : null)
                    );
                else {
                    log.error("Invalid Transaction Format: " + record);
                }
            });
        } catch (IOException | URISyntaxException ex) {
            log.error("Error reading transactions.csv", ex);
            throw ex;
        }

    }

    public void setTrxRecords(List<TransactionRecord> trxRecords) {
        this.trxRecords = trxRecords;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private void loadInputArgs() {
        Scanner sc = new Scanner(System.in);

        System.out.print("accountId: ");
        accountId = sc.nextLine().trim();

        try {
            System.out.print("from: ");
            startTime = LocalDateTime.parse(sc.nextLine().trim(), TransactionUtils.dateTimeFormatter);

            System.out.print("to: ");
            endTime = LocalDateTime.parse(sc.nextLine().trim(), TransactionUtils.dateTimeFormatter);
        } catch (DateTimeParseException dpe) {
            log.error("Invalid Date Input", dpe);
            throw dpe;
        }
    }

}
