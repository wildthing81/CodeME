package com.me.calculator;

import com.me.utils.TransactionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountBalanceCalculatorTest {

    private  AccountBalanceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AccountBalanceCalculator();
        calculator.setAccountId("ME1");
        calculator.setStartTime(LocalDateTime.parse("20/10/2018 12:00:00", TransactionUtils.dateTimeFormatter));
        calculator.setEndTime(LocalDateTime.parse("21/10/2018 10:00:00",TransactionUtils.dateTimeFormatter));
    }


    @Test
    void testInputCSVFileExists() throws URISyntaxException, IOException {

        URL csvUrl = getClass().getClassLoader().getResource("transactions.csv");
        assertNotNull(csvUrl);

        Path csvPath = Paths.get(csvUrl.toURI());
        assertTrue(Files.isReadable(csvPath));

        Stream<String> stream = Files.lines(csvPath);
        assertNotNull(stream);
    }


    @Test
    void testInputCSVFileNotExists() {

        URL csvUrl = getClass().getClassLoader().getResource("src/transactions.txt");
        assertNull(csvUrl);
    }


    @Test
    void testAccountBalanceHappyPath() {

        List<TransactionRecord> trxRecords = new ArrayList<>();

        trxRecords.add(new TransactionRecord("TX12", "ME2", "ME1",
                "20/10/2018 13:00:15",
                "15.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX13", "ME3", "ME1",
               "20/10/2018 15:20:00",
                "20.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX14", "ME1", "ME2",
                "21/10/2018 09:00:00",
                "10.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX15", "ME1", "ME3",
               "21/10/2018 11:00:00",
                "12.0", "PAYMENT" ,null));

        calculator.setTrxRecords(trxRecords);

        AbstractMap.SimpleImmutableEntry<Double,Integer> output  = calculator.findAccountBalance();
        assertEquals("$25.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(3),output.getValue(), "number of transactions");
    }

    @Test
    void testAccountBalanceHappyPath2() {

        List<TransactionRecord> trxRecords = new ArrayList<>();

        trxRecords.add(new TransactionRecord("TX12", "ME2", "ME1",
              "20/10/2018 13:00:15",
                "15.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX13", "ME3", "ME1",
              "20/10/2018 15:20:00",
                "20.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX14", "ME1", "ME2",
               "21/10/2018 09:00:00",
                "50.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX15", "ME1", "ME3",
                "21/10/2018 11:00:00",
                "12.0", "PAYMENT" ,null));

        calculator.setTrxRecords(trxRecords);

        AbstractMap.SimpleImmutableEntry<Double,Integer> output  = calculator.findAccountBalance();
        assertEquals("-$15.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(3),output.getValue(), "number of transactions");
    }


    @Test
    void testOmitReversedTrxAffectingBalance() {

        List<TransactionRecord> trxRecords = new ArrayList<>();

        trxRecords.add(new TransactionRecord("TX12", "ME2", "ME1",
               "20/10/2018 13:00:15",
                "15.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX13", "ME3", "ME1",
                "20/10/2018 15:20:00",
                "20.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX14", "ME1", "ME2",
               "21/10/2018 09:00:00",
                "10.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX15", "ME1", "ME3",
                "21/10/2018 11:00:00",
                "12.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX16", "ME3", "ME1",
               "21/10/2018 11:00:00",
                "20.0", "REVERSAL" ,"TX13"));

        trxRecords.add(new TransactionRecord("TX17", "ME1", "ME2",
               "21/10/2018 09:00:00",
                "10.0", "REVERSAL" ,"TX14"));

        calculator.setTrxRecords(trxRecords);

        AbstractMap.SimpleImmutableEntry<Double,Integer> output  = calculator.findAccountBalance();
        assertEquals("$15.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(1),output.getValue(), "number of transactions");


    }


    @Test
    void testReversedTrxNotAffectingBalance() {

        List<TransactionRecord> trxRecords = new ArrayList<>();

        trxRecords.add(new TransactionRecord("TX12", "ME2", "ME1",
               "20/10/2018 13:00:15",
                "15.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX13", "ME3", "ME1",
               "20/10/2018 15:20:00",
                "20.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX14", "ME1", "ME2",
                "21/10/2018 09:00:00",
                "10.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX15", "ME1", "ME3",
                "21/10/2018 11:00:00",
                "12.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX16", "ME3", "ME1",
                "21/10/2018 11:00:00",
                "20.0", "REVERSAL" ,"TX13"));

        trxRecords.add(new TransactionRecord("TX17", "ME1", "ME2",
               "21/10/2018 10:30:00",
                "10.0", "PAYMENT",null ));

        trxRecords.add(new TransactionRecord("TX18", "ME1", "ME2",
                "21/10/2018 15:00:00",
                "10.0", "REVERSAL" ,"TX17"));

        calculator.setTrxRecords(trxRecords);

        AbstractMap.SimpleImmutableEntry<Double,Integer> output  = calculator.findAccountBalance();
        assertEquals("$5.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(2),output.getValue(), "number of transactions");


    }


    @Test
    void testAccountBalanceZeroTrx() {

        List<TransactionRecord> trxRecords = new ArrayList<>();

        trxRecords.add(new TransactionRecord("TX12", "ME2", "ME3",
               "20/10/2018 13:00:15",
                "15.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX13", "ME3", "ME5",
                "20/10/2018 15:20:00",
                "20.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX14", "ME5", "ME2",
               "21/10/2018 09:00:00", "10.0", "PAYMENT" ,null));

        trxRecords.add(new TransactionRecord("TX15", "ME5", "ME4",
                "21/10/2018 11:00:00",
                "12.0", "PAYMENT" ,null));

        calculator.setTrxRecords(trxRecords);

        AbstractMap.SimpleImmutableEntry<Double,Integer> output  = calculator.findAccountBalance();
        assertEquals("$0.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(0),output.getValue(), "number of transactions");

    }

    @Test
    void testInputCSVFileNotReadable() throws IOException, URISyntaxException {
        URL csvUrl = getClass().getClassLoader().getResource("transactions.csv");
        assertNotNull(csvUrl);

        Path csvPath = Paths.get(csvUrl.toURI());
        Files.setPosixFilePermissions(csvPath, PosixFilePermissions.fromString("---r-xr-x"));
        assertFalse(Files.isReadable(csvPath));

        Assertions.assertThrows(IOException.class, () -> {
            calculator.loadTransactions();
        });

        Files.setPosixFilePermissions(csvPath, PosixFilePermissions.fromString("rwxr-xr-x"));
    }

    @Test
    void testLoadTransactionsFromCSV() throws IOException, URISyntaxException {
        calculator.setAccountId("ACC334455");
        calculator.setStartTime(LocalDateTime.parse("20/10/2018 12:00:00", TransactionUtils.dateTimeFormatter));
        calculator.setEndTime(LocalDateTime.parse("20/10/2018 19:00:00",TransactionUtils.dateTimeFormatter));

        calculator.loadTransactions();
        AbstractMap.SimpleImmutableEntry<Double,Integer> output =  calculator.findAccountBalance();
        assertEquals("-$25.00",
                TransactionUtils.currencyFormatter.format(output.getKey()), "account balance");
        assertEquals(Integer.valueOf(1),output.getValue(), "number of transactions");
    }
}

